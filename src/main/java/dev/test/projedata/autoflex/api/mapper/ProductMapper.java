package dev.test.projedata.autoflex.api.mapper;

import dev.test.projedata.autoflex.api.domain.Product;
import dev.test.projedata.autoflex.api.dtos.request.ProductRequest;
import dev.test.projedata.autoflex.api.dtos.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ProductMaterialMapper productMaterialMapper;

    public ProductMapper(ProductMaterialMapper productMaterialMapper) {
        this.productMaterialMapper = productMaterialMapper;
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getProductMaterials()
                        .stream().map(productMaterialMapper::toResponse)
                        .toList()
        );
    }

    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setPrice(request.price());
        return product;
    }

}
