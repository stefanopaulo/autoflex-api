package dev.test.projedata.autoflex.api.dtos.response;

import dev.test.projedata.autoflex.api.domain.ProductMaterial;

import java.math.BigDecimal;
import java.util.List;

public record RawMaterialResponse(

        Long id,

        String name,

        BigDecimal stockQuantity,

        List<ProductMaterial> products
) {
}
