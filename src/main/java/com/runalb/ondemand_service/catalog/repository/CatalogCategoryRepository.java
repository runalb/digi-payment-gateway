package com.runalb.ondemand_service.catalog.repository;

import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogCategoryRepository extends JpaRepository<CatalogCategoryEntity, Long> {

    Optional<CatalogCategoryEntity> findByIdAndActiveTrue(Long id);

    List<CatalogCategoryEntity> findByActiveTrue(Sort sort);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
