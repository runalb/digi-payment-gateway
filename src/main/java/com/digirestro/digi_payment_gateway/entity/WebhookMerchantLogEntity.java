package com.digirestro.digi_payment_gateway.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "webhook_merchant_log")
public class WebhookMerchantLogEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "webhook_incoming_log_id")
    private WebhookIncomingLogEntity webhookIncomingLog;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_channel_id", nullable = false)
    private PaymentChannelEntity paymentChannel;

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
