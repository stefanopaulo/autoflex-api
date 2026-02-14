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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductMaterialRepository productMaterialRepository;

    @Mock
    private ProductMaterialMapper productMaterialMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAll_whenExistsProducts_shouldReturnPageOfProductResponse() {
        // Given
        Product p1 = new Product(1L, "prod 1", new BigDecimal("150.00"));
        Product p2 = new Product(2L, "prod 2", new BigDecimal("50.00"));
        Product p3 = new Product(3L, "prod 3", new BigDecimal("250.00"));

        List<Product> productList = List.of(p1, p2, p3);

        Pageable pageable = PageRequest.of(0,10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        ProductResponse resp1 = new ProductResponse(1L, "prod 1", new BigDecimal("150.00"), List.of());
        ProductResponse resp2 = new ProductResponse(2L, "prod 2", new BigDecimal("50.00"), List.of());
        ProductResponse resp3 = new ProductResponse(3L, "prod 3", new BigDecimal("250.00"), List.of());

        when(productMapper.toResponse(p1)).thenReturn(resp1);
        when(productMapper.toResponse(p2)).thenReturn(resp2);
        when(productMapper.toResponse(p3)).thenReturn(resp3);


        // When
        Page<ProductResponse> result = productService.findAll(pageable);

        // Then
        assertEquals(3, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().id());
        verify(productRepository).findAll(pageable);
        verify(productMapper, times(3)).toResponse(any());
    }

    @Test
    void findAll_whenNotExistsProducts_shouldReturnEmptyPage() {
        // Given
        List<Product> productList = List.of();

        Pageable pageable = PageRequest.of(0,10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, 0);

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.findAll(pageable);

        // Then
        assertTrue(result.isEmpty());
        verify(productRepository).findAll(pageable);
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void findById_whenExistsProduct_shouldReturnProductResponse() {
        // Given
        Product product = new Product(1L, "prod 1", new BigDecimal("150.00"));
        ProductResponse resp = new ProductResponse(1L, "prod 1", new BigDecimal("150.00"), List.of());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(resp);

        // When
        ProductResponse result = productService.findById(1L);

        // Then
        assertEquals(resp, result);
        verify(productRepository).findById(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    void findById_whenNotExistsProduct_shouldThrowResourceNotFoundException() {
        // Given
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> productService.findById(productId));
        verify(productRepository).findById(productId);
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void insert_whenValidRequest_shouldReturnProductResponse() {
        // Given
        Product savedProduct = new Product(1L, "prod 1", new BigDecimal("150.00"));
        ProductRequest request = new ProductRequest("prod 1", new BigDecimal("150.00"));
        ProductResponse response = new ProductResponse(1L, "prod 1", new BigDecimal("150.00"), List.of());
        Product productWhitOutId = new Product(null, "prod 1", new BigDecimal("150.00"));

        when(productMapper.toEntity(request)).thenReturn(productWhitOutId);
        when(productRepository.save(productWhitOutId)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(response);

        // When
        ProductResponse result = productService.insert(request);

        // Then
        assertEquals(response, result);
        verify(productMapper).toEntity(request);
        verify(productRepository, times(1)).save(productWhitOutId);
        verify(productMapper).toResponse(savedProduct);
    }

    @Test
    void update_whenExistsProduct_shouldReturnProductResponse() {
        // Given
        Long productId = 1L;

        Product existingProduct = new Product(1L, "prod 1", new BigDecimal("150.00"));
        ProductRequest request = new ProductRequest("prod 2", new BigDecimal("350.00"));
        Product updatedProduct = new Product(1L, request.name(), request.price());
        ProductResponse response = new ProductResponse(1L, updatedProduct.getName(), updatedProduct.getPrice(), List.of());

        when(productRepository.getReferenceById(productId)).thenReturn(existingProduct);
        when(productMapper.toResponse(updatedProduct)).thenReturn(response);

        // When
        ProductResponse result = productService.update(productId, request);

        // Then
        assertEquals(response, result);
        assertEquals(request.price(), result.price());
        verify(productRepository).getReferenceById(productId);
        verify(productMapper).toResponse(updatedProduct);
    }

    @Test
    void update_whenNotExistsProduct_shouldThrowResourceNotFoundException() {
        // Given
        Long productId = 999L;

        ProductRequest request = new ProductRequest("prod 2", new BigDecimal("350.00"));

        when(productRepository.getReferenceById(productId)).thenThrow(EntityNotFoundException.class);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> productService.update(productId, request));
        verify(productRepository).getReferenceById(productId);
        verify(productMapper, never()).toResponse(any());
    }

    @Test
    void delete_whenExistsProduct_shouldDeleteSuccessfully() {
        // Given
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId);

        // When
        productService.delete(productId);

        // Then
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void delete_whenNotExistsProduct_shouldThrowResourceNotFoundException() {
        // Given
        Long productId = 999L;

        when(productRepository.existsById(productId)).thenReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> productService.delete(productId));
        verify(productRepository).existsById(productId);
        verify(productRepository, never()).deleteById(productId);
    }

    @Test
    void addMaterial_whenExistsProductAndMaterial_andNotExistsProductMaterial_shouldReturnProductMaterialResponse() {
        // Given
        Long productId = 1L;
        Long rawMaterialId = 3L;

        Product product = new Product(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterial rawMaterial = new RawMaterial(3L, "material 3", new BigDecimal("30"));
        ProductMaterial productMaterial = new ProductMaterial(1L, product, rawMaterial, new BigDecimal("5"));
        ProductMaterialResponse response = new ProductMaterialResponse(productMaterial.getId(), rawMaterial.getId(), rawMaterial.getName(), productMaterial.getQuantityRequired());
        ProductMaterialRequest request = new ProductMaterialRequest(rawMaterialId, new BigDecimal("5"));

        when(productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId)).thenReturn(Optional.empty());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(rawMaterialRepository.findById(rawMaterialId)).thenReturn(Optional.of(rawMaterial));
        when(productMaterialRepository.save(any(ProductMaterial.class))).thenReturn(productMaterial);
        when(productMaterialMapper.toResponse(productMaterial)).thenReturn(response);

        // When
        ProductMaterialResponse result = productService.addMaterial(productId, request);

        // Then
        assertEquals(response, result);
        verify(productMaterialRepository).findByProductIdAndRawMaterialId(productId, rawMaterialId);
        verify(productRepository).findById(productId);
        verify(rawMaterialRepository).findById(rawMaterialId);
        verify(productMaterialRepository).save(any(ProductMaterial.class));
        verify(productMaterialMapper).toResponse(productMaterial);
    }

    @Test
    void addMaterial_whenExistsProductMaterial_shouldThrowDatabaseException() {
        // Given
        Long productId = 1L;
        Long rawMaterialId = 3L;

        ProductMaterial productMaterial = new ProductMaterial();
        ProductMaterialRequest request = new ProductMaterialRequest(rawMaterialId, new BigDecimal("5"));

        when(productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId)).thenReturn(Optional.of(productMaterial));

        // When/Then
        assertThrows(DatabaseException.class, () -> productService.addMaterial(productId, request));
        verify(rawMaterialRepository, never()).findById(any());
        verify(productRepository, never()).findById(any());
        verify(productMaterialRepository, never()).save(any(ProductMaterial.class));
    }

    @Test
    void updateQuantityMaterial_whenExistsProductMaterial_shouldReturnProductMaterialResponse() {
        // Given
        Long productId = 1L;
        Long rawMaterialId = 3L;

        Product product = new Product(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterial rawMaterial = new RawMaterial(3L, "material 3", new BigDecimal("30"));
        ProductMaterial productMaterial = new ProductMaterial(1L, product, rawMaterial, new BigDecimal("5"));
        ProductMaterialResponse response = new ProductMaterialResponse(productMaterial.getId(), rawMaterial.getId(), rawMaterial.getName(), productMaterial.getQuantityRequired());
        ProductMaterialUpdateRequest request = new ProductMaterialUpdateRequest(new BigDecimal("5"));

        when(productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId)).thenReturn(Optional.of(productMaterial));
        when(productMaterialMapper.toResponse(productMaterial)).thenReturn(response);

        // When
        ProductMaterialResponse result = productService.updateQuantityMaterial(productId, rawMaterialId, request);

        // Then
        assertEquals(response, result);
        assertEquals(request.quantityRequired(), result.quantityRequired());
        verify(productMaterialRepository).findByProductIdAndRawMaterialId(productId, rawMaterialId);
        verify(productMaterialMapper).toResponse(productMaterial);
    }

    @Test
    void updateQuantityMaterial_whenNotExistsProductMaterial_shouldThrowResourceNotFoundException() {
        // Given
        Long productId = 999L;
        Long rawMaterialId = 3L;

        ProductMaterialUpdateRequest request = new ProductMaterialUpdateRequest(new BigDecimal("5"));

        when(productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateQuantityMaterial(productId, rawMaterialId, request));
        verify(productMaterialRepository).findByProductIdAndRawMaterialId(productId, rawMaterialId);
        verify(productMaterialMapper, never()).toResponse(any());
    }

    @Test
    void deleteMaterial_whenExistsProductMaterial_shouldDeleteSuccessfully() {
        // Given
        Long productId = 1L;
        Long rawMaterialId = 3L;

        Product product = new Product(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterial rawMaterial = new RawMaterial(3L, "material 3", new BigDecimal("30"));
        ProductMaterial productMaterial = new ProductMaterial(1L, product, rawMaterial, new BigDecimal("5"));

        when(productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId)).thenReturn(Optional.of(productMaterial));

        // When
        productService.deleteMaterial(productId, rawMaterialId);

        // Then
        verify(productMaterialRepository).findByProductIdAndRawMaterialId(productId, rawMaterialId);
        verify(productMaterialRepository).delete(productMaterial);
    }

    @Test
    void deleteMaterial_whenNotExistsProductMaterial_shouldThrowResourceNotFoundException() {
        // Given
        Long productId = 999L;
        Long rawMaterialId = 3L;

        when(productMaterialRepository.findByProductIdAndRawMaterialId(productId, rawMaterialId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteMaterial(productId, rawMaterialId));
        verify(productMaterialRepository).findByProductIdAndRawMaterialId(productId, rawMaterialId);
        verify(productMaterialRepository, never()).deleteById(any());
    }

    @Test
    void getAvailableProduction_whenExistsRelationshipBetweenProductAndRawMaterial_shouldReturnListOfProductProductionResponse() {
        // Given
        Product p1 = new Product(1L, "prod 1", new BigDecimal("150.00"));
        Product p2 = new Product(2L, "prod 2", new BigDecimal("50.00"));

        RawMaterial rm1 = new RawMaterial(1L, "material 1", new BigDecimal("30"));
        RawMaterial rm2 = new RawMaterial(2L, "material 2", new BigDecimal("15"));

        ProductMaterial pm1 = new ProductMaterial(1L, p1, rm1, new BigDecimal("5"));
        ProductMaterial pm2 = new ProductMaterial(2L, p1, rm2, new BigDecimal("5"));
        p1.getProductMaterials().addAll(List.of(pm1, pm2));

        ProductMaterial pm3 = new ProductMaterial(3L, p2, rm1, new BigDecimal("15"));
        p2.getProductMaterials().add(pm3);

        when(productRepository.findAllWithMaterials()).thenReturn(List.of(p1, p2));

        // When
        List<ProductProductionResponse> result = productService.getAvailableProduction();

        // Then
        assertEquals(2, result.size());
        assertEquals("prod 1", result.get(0).productName());
        assertEquals(3, result.get(0).maxProductProduction());
        assertEquals("prod 2", result.get(1).productName());
        assertEquals(2, result.get(1).maxProductProduction());

        verify(productRepository).findAllWithMaterials();
    }
}
