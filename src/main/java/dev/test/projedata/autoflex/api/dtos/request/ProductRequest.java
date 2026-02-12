package dev.test.projedata.autoflex.api.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "cannot be empty")
        String name,

        @NotNull(message = "cannot be empty")
        @Positive(message = "must be greater than zero")
        BigDecimal price

) {
}
