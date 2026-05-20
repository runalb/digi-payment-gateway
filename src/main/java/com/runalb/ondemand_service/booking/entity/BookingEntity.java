package com.runalb.ondemand_service.booking.entity;

import com.runalb.ondemand_service.booking.enums.BookingStatusEnum;
import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.common.persistence.AuditableEntity;
import com.runalb.ondemand_service.offering.entity.BusinessOfferingEntity;
import com.runalb.ondemand_service.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "booking")
public class BookingEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_offering_id", nullable = false)
    private BusinessOfferingEntity businessOffering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private BusinessEntity business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "catalog_service_id", nullable = false)
    private CatalogServiceEntity catalogService;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BookingStatusEnum status = BookingStatusEnum.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;
}
