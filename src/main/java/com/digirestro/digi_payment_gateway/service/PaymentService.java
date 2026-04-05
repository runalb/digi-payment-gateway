package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.repository.PaymentRepository;
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
    public PaymentEntity findByIdAndMerchant_Id(Long id, Long merchantId) {
        return paymentRepository.findByIdAndMerchant_Id(id, merchantId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
    }

    @Transactional(readOnly = true)
    public List<PaymentEntity> findAllByMerchant_IdOrderByCreatedDateTimeDesc(Long merchantId) {
        return paymentRepository.findAllByMerchant_IdOrderByCreatedDateTimeDesc(merchantId);
    }

    @Transactional
    public PaymentEntity save(PaymentEntity payment) {
        return paymentRepository.save(payment);
    }
}
