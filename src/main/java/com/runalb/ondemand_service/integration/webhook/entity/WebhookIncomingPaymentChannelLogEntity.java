package com.runalb.ondemand_service.integration.webhook.entity;

import com.runalb.ondemand_service.common.persistence.AuditableEntity;
import com.runalb.ondemand_service.merchant.entity.MerchantEntity;
import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
import com.runalb.ondemand_service.payment.entity.PaymentEntity;

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
@Table(name = "webhook_incoming_payment_channel_log")
public class WebhookIncomingPaymentChannelLogEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    @ManyToOne
    @JoinColumn(name = "payment_channel_id")
    private PaymentChannelEntity paymentChannel;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private MerchantEntity merchant;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawPayload;

    @Column(nullable = false)
    private String status;
}
