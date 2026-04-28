package com.runalb.ondemand_service.merchant.entity;

import com.runalb.ondemand_service.common.persistence.AuditableEntity;
// import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant_payment_channel_config")
public class MerchantPaymentChannelConfigEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private MerchantEntity merchant;

    // @ManyToOne(optional = false)
    // @JoinColumn(name = "payment_channel_id", nullable = false)
    // private PaymentChannelEntity paymentChannel;

    @Column(nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Column(columnDefinition = "TEXT")
    private String configJson;

}
