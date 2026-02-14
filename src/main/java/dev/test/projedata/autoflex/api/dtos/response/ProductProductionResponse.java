package dev.test.projedata.autoflex.api.dtos.response;

public record ProductProductionResponse(
        Long productId,

        String productName,

        Integer maxProductProduction
) {
}
