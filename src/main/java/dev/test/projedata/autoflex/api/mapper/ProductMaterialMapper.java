package dev.test.projedata.autoflex.api.mapper;

import dev.test.projedata.autoflex.api.domain.ProductMaterial;
import dev.test.projedata.autoflex.api.dtos.response.ProductMaterialResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMaterialMapper {

    public ProductMaterialResponse toResponse(ProductMaterial productMaterial) {
        return new ProductMaterialResponse(
                productMaterial.getId(),
                productMaterial.getRawMaterial().getId(),
                productMaterial.getRawMaterial().getName(),
                productMaterial.getQuantityRequired()
        );
    }
    
}
