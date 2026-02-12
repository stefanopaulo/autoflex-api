package dev.test.projedata.autoflex.api.service;

import dev.test.projedata.autoflex.api.domain.Product;
import dev.test.projedata.autoflex.api.dtos.request.ProductRequest;
import dev.test.projedata.autoflex.api.dtos.response.ProductResponse;
import dev.test.projedata.autoflex.api.exceptions.ResourceNotFoundException;
import dev.test.projedata.autoflex.api.mapper.ProductMapper;
import dev.test.projedata.autoflex.api.repository.ProductRepository;
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
}
