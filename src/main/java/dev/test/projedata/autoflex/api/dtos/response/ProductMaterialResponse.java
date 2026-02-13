package dev.test.projedata.autoflex.api.dtos.response;

import java.math.BigDecimal;

public record ProductMaterialResponse(
        Long id,

        Long rawMaterialId,

        String rawMaterialName,

        BigDecimal quantityRequired

) {
}
