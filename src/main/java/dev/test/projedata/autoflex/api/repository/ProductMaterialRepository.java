package dev.test.projedata.autoflex.api.repository;

import dev.test.projedata.autoflex.api.domain.ProductMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMaterialRepository extends JpaRepository<ProductMaterial, Long> {

    Optional<ProductMaterial> findByProductIdAndRawMaterialId(Long productId, Long rawMaterialId);
    List<ProductMaterial> findByProductId(Long productId);

}
