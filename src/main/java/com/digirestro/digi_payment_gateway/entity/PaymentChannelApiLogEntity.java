package com.digirestro.digi_payment_gateway.entity;

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
@Table(name = "payment_channel_api_log")
public class PaymentChannelApiLogEntity extends AuditableEntity {

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

    @ManyToOne
    @JoinColumn(name = "merchant_payment_channel_config_id")
    private MerchantPaymentChannelConfigEntity merchantPaymentChannelConfig;

    @Column(nullable = false)
    private String operation;

    @Column(nullable = false)
    private String requestMethod;

    @Column(nullable = false)
    private String requestUrl;

    @Column(columnDefinition = "TEXT")
    private String requestHeaders;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    private Integer responseStatusCode;

    @Column(columnDefinition = "TEXT")
    private String responseHeaders;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private Integer durationMs;
}
