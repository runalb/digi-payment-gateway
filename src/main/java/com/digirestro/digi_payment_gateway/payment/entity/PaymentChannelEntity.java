package com.digirestro.digi_payment_gateway.payment.entity;

import com.digirestro.digi_payment_gateway.common.persistence.AuditableEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentChannelNameEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment_channel")
public class PaymentChannelEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PaymentChannelNameEnum name;

    @Column(nullable = false)
    private Boolean isActive = Boolean.TRUE;

}
