package dev.test.projedata.autoflex.api.service;

import dev.test.projedata.autoflex.api.domain.Product;
import dev.test.projedata.autoflex.api.dtos.request.ProductRequest;
import dev.test.projedata.autoflex.api.dtos.response.ProductResponse;
import dev.test.projedata.autoflex.api.mapper.ProductMapper;
import dev.test.projedata.autoflex.api.repository.ProductRepository;
import dev.test.projedata.autoflex.api.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
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
}
