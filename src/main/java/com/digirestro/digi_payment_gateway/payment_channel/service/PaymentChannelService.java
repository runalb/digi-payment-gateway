package com.digirestro.digi_payment_gateway.payment_channel.service;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digirestro.digi_payment_gateway.payment_channel.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.payment_channel.repository.PaymentChannelRepository;

@Service
public class PaymentChannelService {

    private final PaymentChannelRepository paymentChannelRepository;

    public PaymentChannelService(PaymentChannelRepository paymentChannelRepository) {
        this.paymentChannelRepository = paymentChannelRepository;
    }

    @Transactional(readOnly = true)
    public PaymentChannelEntity findById(Long id) {
        return paymentChannelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment channel not found: " + id));
    }

    @Transactional(readOnly = true)
    public PaymentChannelEntity findByName(PaymentChannelNameEnum name) {
        return paymentChannelRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Payment channel " + name + " not found in database"));
    }
}
