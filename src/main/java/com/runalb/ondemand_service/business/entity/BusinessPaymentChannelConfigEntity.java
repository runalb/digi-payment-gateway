package com.runalb.ondemand_service.business.entity;

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
@Table(name = "business_payment_channel_config")
public class BusinessPaymentChannelConfigEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private BusinessEntity business;

    // @ManyToOne(optional = false)
    // @JoinColumn(name = "payment_channel_id", nullable = false)
    // private PaymentChannelEntity paymentChannel;

    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    @Column(columnDefinition = "TEXT")
    private String configJson;

}
