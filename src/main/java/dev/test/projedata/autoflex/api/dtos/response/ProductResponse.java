package dev.test.projedata.autoflex.api.dtos.response;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(

        Long id,

        String name,

        BigDecimal price,

        List<ProductMaterialResponse> materials
) {
}
