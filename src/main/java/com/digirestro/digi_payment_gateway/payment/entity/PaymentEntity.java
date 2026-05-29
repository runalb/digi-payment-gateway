package com.digirestro.digi_payment_gateway.payment.entity;

import com.digirestro.digi_payment_gateway.common.persistence.AuditableEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentOriginEnum;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentTypeEnum;
import com.digirestro.digi_payment_gateway.payment_channel.entity.PaymentChannelEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class PaymentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false, columnDefinition = "uuid", unique = true)
    // private UUID paymentReferenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentTypeEnum paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentOriginEnum paymentOrigin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private MerchantEntity merchant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "merchant_payment_channel_config_id", nullable = false)
    private MerchantPaymentChannelConfigEntity merchantPaymentChannelConfig;

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_channel_id", nullable = false)
    private PaymentChannelEntity paymentChannel;

    @Column(nullable = false)
    private String merchantReferenceId;

    private String paymentChannelTxnId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusEnum status = PaymentStatusEnum.INITIATED;

    @Column(columnDefinition = "TEXT")
    private String paymentChannelCheckoutUrl;

    // @Column(columnDefinition = "TEXT")
    // private String merchantMetadataJson;

    @Column(columnDefinition = "TEXT")
    private String redirectSuccessUrl;

    @Column(columnDefinition = "TEXT")
    private String redirectFailureUrl;

    @Column(name = "http_exchange_correlation_id", length = 64)
    private String httpExchangeCorrelationId;

    // @Column(nullable = false)
    // private Integer attempts = 0;

}
