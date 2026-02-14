package dev.test.projedata.autoflex.api.configs;

import dev.test.projedata.autoflex.api.domain.Product;
import dev.test.projedata.autoflex.api.domain.RawMaterial;
import dev.test.projedata.autoflex.api.repository.ProductRepository;
import dev.test.projedata.autoflex.api.repository.RawMaterialRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public TestConfig(ProductRepository productRepository, RawMaterialRepository rawMaterialRepository) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Product p1 = new Product(null, "prod 1", new BigDecimal("150.00"));
        Product p2 = new Product(null, "prod 2", new BigDecimal("50.00"));
        Product p3 = new Product(null, "prod 3", new BigDecimal("250.00"));

        RawMaterial rm1 = new RawMaterial(null, "material 1", new BigDecimal("30"));
        RawMaterial rm2 = new RawMaterial(null, "material 2", new BigDecimal("15"));
        RawMaterial rm3 = new RawMaterial(null, "material 3", new BigDecimal("50"));

        productRepository.saveAll(List.of(p1, p2, p3));
        rawMaterialRepository.saveAll(List.of(rm1, rm2, rm3));
    }
}
