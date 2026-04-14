package com.digirestro.digi_payment_gateway.portal.merchant.entity;

import com.digirestro.digi_payment_gateway.entity.AuditableEntity;

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
@Table(name = "merchant_config")
public class MerchantConfigEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "merchant_id", nullable = false, unique = true)
    private MerchantEntity merchant;

    @Column(columnDefinition = "TEXT")
    private String webhookUrl;

    /** ISO 4217 alphabetic code (e.g. USD, EUR). */
    @Column(nullable = false, length = 3)
    private String currency;
}
