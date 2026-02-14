package dev.test.projedata.autoflex.api.repository;

import dev.test.projedata.autoflex.api.domain.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
}
