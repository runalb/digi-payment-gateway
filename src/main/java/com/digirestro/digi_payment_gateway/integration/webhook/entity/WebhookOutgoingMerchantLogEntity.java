package com.digirestro.digi_payment_gateway.integration.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import com.digirestro.digi_payment_gateway.common.persistence.AuditableEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "webhook_outgoing_merchant_log")
public class WebhookOutgoingMerchantLogEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "webhook_incoming_payment_channel_log_id", nullable = false)
    private WebhookIncomingPaymentChannelLogEntity webhookIncomingPaymentChannelLog;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_channel_id", nullable = false)
    private PaymentChannelEntity paymentChannel;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private MerchantEntity merchant;

    @Column(nullable = false)
    private String webhookUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer retryCount = 0;

    private LocalDateTime lastAttemptAt;
}
