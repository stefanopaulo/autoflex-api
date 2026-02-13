package dev.test.projedata.autoflex.api.dtos.response;

import java.math.BigDecimal;

public record RawMaterialResponse(

        Long id,

        String name,

        BigDecimal stockQuantity
) {
}
