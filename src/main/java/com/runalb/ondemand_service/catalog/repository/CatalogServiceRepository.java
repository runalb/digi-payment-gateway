package com.runalb.ondemand_service.catalog.repository;

import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogServiceRepository extends JpaRepository<CatalogServiceEntity, Long> {

    boolean existsByCatalogCategory_IdAndNameIgnoreCase(Long catalogCategoryId, String name);

    boolean existsByCatalogCategory_IdAndNameIgnoreCaseAndIdNot(
            Long catalogCategoryId, String name, Long id);

    List<CatalogServiceEntity> findByCatalogCategory_Id(Long catalogCategoryId);

    @EntityGraph(attributePaths = "catalogCategory")
    List<CatalogServiceEntity> findAllByIsDeletedFalse(Sort sort);

    @EntityGraph(attributePaths = "catalogCategory")
    List<CatalogServiceEntity> findByCatalogCategory_IdAndIsDeletedFalseOrderByDisplayOrderAscIdAsc(
            Long categoryId);

    @EntityGraph(attributePaths = "catalogCategory")
    Optional<CatalogServiceEntity> findWithCatalogCategoryById(Long id);

    @EntityGraph(attributePaths = "catalogCategory")
    Optional<CatalogServiceEntity> findWithCatalogCategoryByIdAndIsDeletedFalse(Long id);
}
