package dev.test.projedata.autoflex.api.service;

import dev.test.projedata.autoflex.api.domain.RawMaterial;
import dev.test.projedata.autoflex.api.dtos.request.RawMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.response.RawMaterialResponse;
import dev.test.projedata.autoflex.api.exceptions.ResourceNotFoundException;
import dev.test.projedata.autoflex.api.mapper.RawMaterialMapper;
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
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private RawMaterialMapper rawMaterialMapper;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    @Test
    void findAll_whenExistsRawMaterials_shouldReturnPageOfRawMaterialResponse() {
        // Given
        RawMaterial rm1 = new RawMaterial(1L, "material 1", new BigDecimal("30"));
        RawMaterial rm2 = new RawMaterial(2L, "material 2", new BigDecimal("15"));
        RawMaterial rm3 = new RawMaterial(3L, "material 3", new BigDecimal("50"));

        List<RawMaterial> rawMaterialList = List.of(rm1, rm2, rm3);

        Pageable pageable = PageRequest.of(0,10);
        Page<RawMaterial> rawMaterialPage = new PageImpl<>(rawMaterialList, pageable, rawMaterialList.size());

        when(rawMaterialRepository.findAll(pageable)).thenReturn(rawMaterialPage);

        RawMaterialResponse resp1 = new RawMaterialResponse(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterialResponse resp2 = new RawMaterialResponse(2L, "prod 2", new BigDecimal("50.00"));
        RawMaterialResponse resp3 = new RawMaterialResponse(3L, "prod 3", new BigDecimal("250.00"));

        when(rawMaterialMapper.toResponse(rm1)).thenReturn(resp1);
        when(rawMaterialMapper.toResponse(rm2)).thenReturn(resp2);
        when(rawMaterialMapper.toResponse(rm3)).thenReturn(resp3);


        // When
        Page<RawMaterialResponse> result = rawMaterialService.findAll(pageable);

        // Then
        assertEquals(3, result.getTotalElements());
        assertEquals(1L, result.getContent().getFirst().id());
        verify(rawMaterialRepository).findAll(pageable);
        verify(rawMaterialMapper, times(3)).toResponse(any());
    }

    @Test
    void findAll_whenNotExistsRawMaterials_shouldReturnEmptyPage() {
        // Given
        List<RawMaterial> rawMaterialList = List.of();

        Pageable pageable = PageRequest.of(0,10);
        Page<RawMaterial> rawMaterialPage = new PageImpl<>(rawMaterialList, pageable, 0);

        when(rawMaterialRepository.findAll(pageable)).thenReturn(rawMaterialPage);

        // When
        Page<RawMaterialResponse> result = rawMaterialService.findAll(pageable);

        // Then
        assertTrue(result.isEmpty());
        verify(rawMaterialRepository).findAll(pageable);
        verify(rawMaterialMapper, never()).toResponse(any());
    }

    @Test
    void findById_whenExistsRawMaterial_shouldReturnRawMaterialResponse() {
        // Given
        RawMaterial rawMaterial = new RawMaterial(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterialResponse resp = new RawMaterialResponse(1L, "prod 1", new BigDecimal("150.00"));

        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));
        when(rawMaterialMapper.toResponse(rawMaterial)).thenReturn(resp);

        // When
        RawMaterialResponse result = rawMaterialService.findById(1L);

        // Then
        assertEquals(resp, result);
        verify(rawMaterialRepository).findById(1L);
        verify(rawMaterialMapper).toResponse(rawMaterial);
    }

    @Test
    void findById_whenNotExistsRawMaterial_shouldThrowResourceNotFoundException() {
        // Given
        Long rawMaterialId = 999L;

        when(rawMaterialRepository.findById(rawMaterialId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.findById(rawMaterialId));
        verify(rawMaterialRepository).findById(rawMaterialId);
        verify(rawMaterialMapper, never()).toResponse(any());
    }

    @Test
    void insert_whenValidRequest_shouldReturnRawMaterialResponse() {
        // Given
        RawMaterial savedRawMaterial = new RawMaterial(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterialRequest request = new RawMaterialRequest("prod 1", new BigDecimal("150.00"));
        RawMaterialResponse response = new RawMaterialResponse(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterial rawMaterialWhitOutId = new RawMaterial(null, "prod 1", new BigDecimal("150.00"));

        when(rawMaterialMapper.toEntity(request)).thenReturn(rawMaterialWhitOutId);
        when(rawMaterialRepository.save(rawMaterialWhitOutId)).thenReturn(savedRawMaterial);
        when(rawMaterialMapper.toResponse(savedRawMaterial)).thenReturn(response);

        // When
        RawMaterialResponse result = rawMaterialService.insert(request);

        // Then
        assertEquals(response, result);
        verify(rawMaterialMapper).toEntity(request);
        verify(rawMaterialRepository, times(1)).save(rawMaterialWhitOutId);
        verify(rawMaterialMapper).toResponse(savedRawMaterial);
    }

    @Test
    void update_whenExistsRawMaterial_shouldReturnRawMaterialResponse() {
        // Given
        Long rawMaterialId = 1L;

        RawMaterial existingRawMaterial = new RawMaterial(1L, "prod 1", new BigDecimal("150.00"));
        RawMaterialRequest request = new RawMaterialRequest("prod 2", new BigDecimal("350.00"));
        RawMaterial updatedRawMaterial = new RawMaterial(1L, request.name(), request.stockQuantity());
        RawMaterialResponse response = new RawMaterialResponse(1L, updatedRawMaterial.getName(), updatedRawMaterial.getStockQuantity());

        when(rawMaterialRepository.getReferenceById(rawMaterialId)).thenReturn(existingRawMaterial);
        when(rawMaterialMapper.toResponse(updatedRawMaterial)).thenReturn(response);

        // When
        RawMaterialResponse result = rawMaterialService.update(rawMaterialId, request);

        // Then
        assertEquals(response, result);
        assertEquals(request.stockQuantity(), result.stockQuantity());
        verify(rawMaterialRepository).getReferenceById(rawMaterialId);
        verify(rawMaterialMapper).toResponse(updatedRawMaterial);
    }

    @Test
    void update_whenNotExistsRawMaterial_shouldThrowResourceNotFoundException() {
        // Given
        Long rawMaterialId = 999L;

        RawMaterialRequest request = new RawMaterialRequest("prod 2", new BigDecimal("350.00"));

        when(rawMaterialRepository.getReferenceById(rawMaterialId)).thenThrow(EntityNotFoundException.class);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.update(rawMaterialId, request));
        verify(rawMaterialRepository).getReferenceById(rawMaterialId);
        verify(rawMaterialMapper, never()).toResponse(any());
    }

    @Test
    void delete_whenExistsRawMaterial_shouldDeleteSuccessfully() {
        // Given
        Long rawMaterialId = 1L;

        when(rawMaterialRepository.existsById(rawMaterialId)).thenReturn(true);
        doNothing().when(rawMaterialRepository).deleteById(rawMaterialId);

        // When
        rawMaterialService.delete(rawMaterialId);

        // Then
        verify(rawMaterialRepository).existsById(rawMaterialId);
        verify(rawMaterialRepository).deleteById(rawMaterialId);
    }

    @Test
    void delete_whenNotExistsRawMaterial_shouldThrowResourceNotFoundException() {
        // Given
        Long rawMaterialId = 999L;

        when(rawMaterialRepository.existsById(rawMaterialId)).thenReturn(false);

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.delete(rawMaterialId));
        verify(rawMaterialRepository).existsById(rawMaterialId);
        verify(rawMaterialRepository, never()).deleteById(rawMaterialId);
    }
}
