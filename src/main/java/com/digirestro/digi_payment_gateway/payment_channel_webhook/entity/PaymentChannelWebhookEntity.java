package com.digirestro.digi_payment_gateway.payment_channel_webhook.entity;

import com.digirestro.digi_payment_gateway.common.persistence.AuditableEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment_channel.entity.PaymentChannelEntity;

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
@Table(name = "payment_channel_webhook")
public class PaymentChannelWebhookEntity extends AuditableEntity {

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
