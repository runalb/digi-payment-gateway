package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.repository.PaymentChannelRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentChannelService {

    private final PaymentChannelRepository paymentChannelRepository;

    public PaymentChannelService(PaymentChannelRepository paymentChannelRepository) {
        this.paymentChannelRepository = paymentChannelRepository;
    }

    @Transactional(readOnly = true)
    public PaymentChannelEntity findByName(PaymentChannelNameEnum name) {
        return paymentChannelRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Payment channel " + name + " not found in database"));
    }
}
