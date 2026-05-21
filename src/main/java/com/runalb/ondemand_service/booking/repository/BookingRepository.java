package com.runalb.ondemand_service.booking.repository;

import com.runalb.ondemand_service.booking.entity.BookingEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @EntityGraph(
            attributePaths = {
                "user",
                "businessOffering",
                "business",
                "catalogService",
                "catalogService.catalogCategory",
                "catalogService.images"
            })
    List<BookingEntity> findByUser_IdAndIsDeletedFalseOrderByCreatedDateTimeDesc(Long userId);

    @EntityGraph(
            attributePaths = {
                "user",
                "businessOffering",
                "business",
                "catalogService",
                "catalogService.catalogCategory",
                "catalogService.images"
            })
    List<BookingEntity> findByBusiness_IdAndIsDeletedFalseOrderByCreatedDateTimeDesc(Long businessId);

    @EntityGraph(
            attributePaths = {
                "user",
                "businessOffering",
                "business",
                "catalogService",
                "catalogService.catalogCategory",
                "catalogService.images"
            })
    Optional<BookingEntity> findByIdAndIsDeletedFalse(Long id);
}
