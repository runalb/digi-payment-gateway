package com.runalb.ondemand_service.business.entity;

import com.runalb.ondemand_service.common.persistence.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "business_config")
public class BusinessConfigEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "business_id", nullable = false, unique = true)
    private BusinessEntity business;

    @Column(columnDefinition = "TEXT")
    private String webhookUrl;

    /** ISO 4217 alphabetic code (e.g. USD, EUR). */
    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;
}
