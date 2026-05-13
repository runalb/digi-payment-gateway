package com.digirestro.digi_payment_gateway.payment.service;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.repository.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public PaymentEntity findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for paymentId: " + id));
    }

    @Transactional(readOnly = true)
    public PaymentEntity findByIdAndMerchantId(Long id, Long merchantId) {
        return paymentRepository.findByIdAndMerchantId(id, merchantId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
    }

    @Transactional(readOnly = true)
    public List<PaymentEntity> findAllByMerchantIdOrderByCreatedDateTimeDesc(Long merchantId) {
        return paymentRepository.findAllByMerchantIdOrderByCreatedDateTimeDesc(merchantId);
    }

    @Transactional
    public PaymentEntity save(PaymentEntity payment) {
        return paymentRepository.save(payment);
    }
}
