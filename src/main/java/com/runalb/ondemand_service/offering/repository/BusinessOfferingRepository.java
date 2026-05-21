package com.runalb.ondemand_service.offering.repository;

import com.runalb.ondemand_service.offering.entity.BusinessOfferingEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessOfferingRepository extends JpaRepository<BusinessOfferingEntity, Long> {

    boolean existsByBusiness_IdAndCatalogService_IdAndIsDeletedFalse(Long businessId, Long catalogServiceId);

    Optional<BusinessOfferingEntity> findByBusiness_IdAndCatalogService_Id(Long businessId, Long catalogServiceId);

    @EntityGraph(attributePaths = {"catalogService", "catalogService.catalogCategory", "catalogService.images"})
    List<BusinessOfferingEntity> findByBusiness_IdAndIsDeletedFalseOrderByIdAsc(Long businessId);

    @EntityGraph(attributePaths = {"catalogService", "catalogService.catalogCategory", "catalogService.images"})
    Optional<BusinessOfferingEntity> findByIdAndBusiness_IdAndIsDeletedFalse(Long id, Long businessId);

    long countByCatalogService_IdAndIsDeletedFalseAndIsActiveTrue(Long catalogServiceId);

    @EntityGraph(
            attributePaths = {
                "business",
                "catalogService",
                "catalogService.catalogCategory",
                "catalogService.images"
            })
    List<BusinessOfferingEntity> findByCatalogService_IdAndIsDeletedFalseAndIsActiveTrueOrderByIdAsc(
            Long catalogServiceId);

    @EntityGraph(
            attributePaths = {
                "business",
                "catalogService",
                "catalogService.catalogCategory",
                "catalogService.images"
            })
    List<BusinessOfferingEntity>
            findByCatalogService_IdAndIsDeletedFalseAndIsActiveTrueAndIsVerifiedTrueOrderByIdAsc(
                    Long catalogServiceId);

    @EntityGraph(
            attributePaths = {
                "business",
                "catalogService",
                "catalogService.catalogCategory",
                "catalogService.images"
            })
    Optional<BusinessOfferingEntity> findByIdAndIsDeletedFalse(Long id);

    @EntityGraph(
            attributePaths = {
                "business",
                "catalogService",
                "catalogService.catalogCategory",
                "catalogService.images"
            })
    List<BusinessOfferingEntity> findByIdInAndIsDeletedFalse(Collection<Long> ids);
}
