package dev.test.projedata.autoflex.api.dtos.response;

import dev.test.projedata.autoflex.api.domain.ProductMaterial;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(

        Long id,

        String name,

        BigDecimal price,

        List<ProductMaterial> materials
) {
}
