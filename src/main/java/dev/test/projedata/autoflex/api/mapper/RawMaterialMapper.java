package dev.test.projedata.autoflex.api.mapper;

import dev.test.projedata.autoflex.api.domain.RawMaterial;
import dev.test.projedata.autoflex.api.dtos.request.RawMaterialRequest;
import dev.test.projedata.autoflex.api.dtos.response.RawMaterialResponse;
import org.springframework.stereotype.Component;

@Component
public class RawMaterialMapper {

    public RawMaterialResponse toResponse(RawMaterial rawMaterial) {
        return new RawMaterialResponse(
                rawMaterial.getId(),
                rawMaterial.getName(),
                rawMaterial.getStockQuantity()
        );
    }

    public RawMaterial toEntity(RawMaterialRequest request) {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setName(request.name());
        rawMaterial.setStockQuantity(request.stockQuantity());
        return rawMaterial;
    }

}
