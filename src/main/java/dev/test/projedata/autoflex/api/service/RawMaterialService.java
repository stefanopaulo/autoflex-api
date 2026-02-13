package dev.test.projedata.autoflex.api.service;

import dev.test.projedata.autoflex.api.domain.RawMaterial;
import dev.test.projedata.autoflex.api.dtos.request.RawMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.response.RawMaterialResponse;
import dev.test.projedata.autoflex.api.exceptions.ResourceNotFoundException;
import dev.test.projedata.autoflex.api.mapper.RawMaterialMapper;
import dev.test.projedata.autoflex.api.repository.RawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RawMaterialMapper rawMaterialMapper;

    public RawMaterialService(RawMaterialRepository rawMaterialRepository, RawMaterialMapper rawMaterialMapper) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.rawMaterialMapper = rawMaterialMapper;
    }

    @Transactional
    public RawMaterialResponse insert(RawMaterialRequest request) {
        RawMaterial rawMaterial = rawMaterialMapper.toEntity(request);

        return rawMaterialMapper.toResponse(rawMaterialRepository.save(rawMaterial));
    }

    public Page<RawMaterialResponse> findAll(Pageable pageable) {
        return rawMaterialRepository.findAll(pageable)
                .map(rawMaterialMapper::toResponse);
    }

    public RawMaterialResponse findById(Long id) {
        return rawMaterialRepository.findById(id)
                .map(rawMaterialMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial not found. Id: " + id));
    }

    @Transactional
    public RawMaterialResponse update(Long id, RawMaterialRequest request) {
        try {
            RawMaterial existingProd = rawMaterialRepository.getReferenceById(id);

            if (request.name() != null) existingProd.setName(request.name());
            if (request.stockQuantity() != null) existingProd.setStockQuantity(request.stockQuantity());

            return rawMaterialMapper.toResponse(existingProd);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("RawMaterial not found. Id: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!rawMaterialRepository.existsById(id)) throw new ResourceNotFoundException("RawMaterial not found. Id: " + id);

        rawMaterialRepository.deleteById(id);
    }
}
