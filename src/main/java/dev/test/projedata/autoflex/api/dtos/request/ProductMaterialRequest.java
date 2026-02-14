package dev.test.projedata.autoflex.api.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductMaterialRequest(

        @NotNull(message = "cannot be empty")
        Long rawMaterialId,

        @NotNull(message = "cannot be empty")
        @Positive(message = "must be greater than zero")
        BigDecimal quantityRequired
) {
}
