package com.runalb.ondemand_service.catalog.repository;

import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CatalogServiceRepository extends JpaRepository<CatalogServiceEntity, Long> {

    boolean existsByCatalogCategory_IdAndNameIgnoreCase(Long catalogCategoryId, String name);

    boolean existsByCatalogCategory_IdAndNameIgnoreCaseAndIdNot(Long catalogCategoryId, String name, Long id);

    List<CatalogServiceEntity> findByCatalogCategory_Id(Long catalogCategoryId);

    @EntityGraph(attributePaths = "catalogCategory")
    List<CatalogServiceEntity> findAllByActiveTrue(Sort sort);

    @EntityGraph(attributePaths = "catalogCategory")
    List<CatalogServiceEntity> findByCatalogCategory_IdAndActiveTrueOrderByDisplayOrderAscIdAsc(Long categoryId);

    @EntityGraph(attributePaths = "catalogCategory")
    @Query("SELECT s FROM CatalogServiceEntity s WHERE s.id = :id")
    Optional<CatalogServiceEntity> findByIdFetchCatalogCategory(@Param("id") Long id);

    @EntityGraph(attributePaths = "catalogCategory")
    @Query("SELECT s FROM CatalogServiceEntity s WHERE s.id = :id AND s.active = true")
    Optional<CatalogServiceEntity> findActiveByIdFetchCatalogCategory(@Param("id") Long id);
}
