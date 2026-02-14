package dev.test.projedata.autoflex.api.service;

import dev.test.projedata.autoflex.api.domain.Product;
import dev.test.projedata.autoflex.api.domain.ProductMaterial;
import dev.test.projedata.autoflex.api.domain.RawMaterial;
import dev.test.projedata.autoflex.api.dtos.request.ProductMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.request.ProductMaterialUpdateRequest;
import dev.test.projedata.autoflex.api.dtos.request.ProductRequest;
import dev.test.projedata.autoflex.api.dtos.response.ProductMaterialResponse;
import dev.test.projedata.autoflex.api.dtos.response.ProductProductionResponse;
import dev.test.projedata.autoflex.api.dtos.response.ProductResponse;
import dev.test.projedata.autoflex.api.exceptions.DatabaseException;
import dev.test.projedata.autoflex.api.exceptions.ResourceNotFoundException;
import dev.test.projedata.autoflex.api.mapper.ProductMapper;
import dev.test.projedata.autoflex.api.mapper.ProductMaterialMapper;
import dev.test.projedata.autoflex.api.repository.ProductMaterialRepository;
import dev.test.projedata.autoflex.api.repository.ProductRepository;
import dev.test.projedata.autoflex.api.repository.RawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductMaterialRepository productMaterialRepository;
    private final ProductMaterialMapper productMaterialMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, RawMaterialRepository rawMaterialRepository, ProductMaterialRepository productMaterialRepository, ProductMaterialMapper productMaterialMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.rawMaterialRepository = rawMaterialRepository;
        this.productMaterialRepository = productMaterialRepository;
        this.productMaterialMapper = productMaterialMapper;
    }

    @Transactional
    public ProductResponse insert(ProductRequest request) {
        Product product = productMapper.toEntity(request);

        return productMapper.toResponse(productRepository.save(product));
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found. Id: " + id));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        try {
            Product existingProd = productRepository.getReferenceById(id);

            if (request.name() != null) existingProd.setName(request.name());
            if (request.price() != null) existingProd.setPrice(request.price());

            return productMapper.toResponse(existingProd);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Product not found. Id: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) throw new ResourceNotFoundException("Product not found. Id: " + id);

        productRepository.deleteById(id);
    }

    @Transactional
    public ProductMaterialResponse addMaterial(Long productId, ProductMaterialRequest request) {
        Optional<ProductMaterial> existing = productMaterialRepository.findByProductIdAndRawMaterialId(productId, request.rawMaterialId());

        if (existing.isPresent()) {
            throw new DatabaseException("Material already associated with this product");
        }

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found. Id: " + productId));
        RawMaterial rawMaterial = rawMaterialRepository.findById(request.rawMaterialId()).orElseThrow(() -> new ResourceNotFoundException("RawMaterial not found. Id: " + request.rawMaterialId()));

        ProductMaterial productMaterial = new ProductMaterial();
        productMaterial.setProduct(product);
        productMaterial.setRawMaterial(rawMaterial);
        productMaterial.setQuantityRequired(request.quantityRequired());

        return productMaterialMapper.toResponse(productMaterialRepository.save(productMaterial));
    }

    @Transactional
    public ProductMaterialResponse updateQuantityMaterial(Long productId, Long rawMaterialId, ProductMaterialUpdateRequest request) {
        ProductMaterial productMaterial = productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId).orElseThrow(() -> new ResourceNotFoundException("ProductMaterial not found"));
        productMaterial.setQuantityRequired(request.quantityRequired());

        return productMaterialMapper.toResponse(productMaterial);
    }

    @Transactional
    public void deleteMaterial(Long productId, Long rawMaterialId) {
        ProductMaterial productMaterial = productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId).orElseThrow(() -> new ResourceNotFoundException("ProductMaterial not found"));

        productMaterialRepository.delete(productMaterial);
    }

    public List<ProductProductionResponse> getAvailableProduction() {
        List<Product> products = productRepository.findAllWithMaterials();

        return products.stream()
                .map((product -> {
                    int maxPossible = product.getProductMaterials().stream()
                            .mapToInt(pm -> {
                                BigDecimal stockAvailable = pm.getRawMaterial().getStockQuantity();
                                BigDecimal requiredPerUnit = pm.getQuantityRequired();

                                return stockAvailable.divide(requiredPerUnit, 0, RoundingMode.FLOOR).intValue();
                            })
                            .min()
                            .orElse(0);

                    return new ProductProductionResponse(product.getId(), product.getName(), maxPossible);
                }))
                .toList();
    }
}
