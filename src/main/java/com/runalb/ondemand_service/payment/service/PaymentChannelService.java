package com.runalb.ondemand_service.payment.service;

import com.runalb.ondemand_service.payment.entity.PaymentChannelEntity;
import com.runalb.ondemand_service.payment.enums.PaymentChannelNameEnum;
import com.runalb.ondemand_service.payment.repository.PaymentChannelRepository;

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
