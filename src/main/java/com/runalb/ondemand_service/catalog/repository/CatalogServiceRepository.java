package com.runalb.ondemand_service.catalog.repository;

import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CatalogServiceRepository extends JpaRepository<CatalogServiceEntity, Long> {

    long countByCatalogCategory_Id(Long categoryId);

    @EntityGraph(attributePaths = "catalogCategory")
    List<CatalogServiceEntity> findByCatalogCategory_IdOrderByDisplayOrderAscIdAsc(Long categoryId);

    @EntityGraph(attributePaths = "catalogCategory")
    @Query("SELECT s FROM CatalogServiceEntity s WHERE s.id = :id")
    Optional<CatalogServiceEntity> findByIdFetchCatalogCategory(@Param("id") Long id);
}
