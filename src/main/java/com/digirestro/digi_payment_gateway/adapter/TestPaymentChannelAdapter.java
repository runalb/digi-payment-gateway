package com.digirestro.digi_payment_gateway.adapter;

import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import com.digirestro.digi_payment_gateway.entity.PaymentChannelEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.repository.PaymentChannelRepository;
import com.digirestro.digi_payment_gateway.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestPaymentChannelAdapter implements PaymentChannelAdapter {

    private final PaymentChannelEntity channel;
    private final PaymentRepository paymentRepository;

    public TestPaymentChannelAdapter(PaymentChannelRepository paymentChannelRepository, PaymentRepository paymentRepository) {
        this.channel = paymentChannelRepository
                .findByName(PaymentChannelNameEnum.TEST)
                .orElseThrow(() -> new IllegalStateException("Payment channel TEST not found in database"));
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentChannelEntity getChannel() {
        return channel;
    }

    @Override
    public AdapterPaymentLinkResponse createPaymentLink(PaymentEntity payment) {
        String paymentChannelTxnId = "TEST-TXN-" + UUID.randomUUID();
        String paymentUrl = "http://localhost:8080/test-payment-link.html?paymentId=" + payment.getId() + "&merchantId=" + payment.getMerchant().getId();
        
        if (!paymentChannelTxnId.isEmpty() && !paymentUrl.isEmpty()) {
            payment.setPaymentChannelTxnId(paymentChannelTxnId);
            payment.setPaymentLinkUrl(paymentUrl);
            payment.setStatus(PaymentStatusEnum.PAYMENT_LINK_GENERATED);
        }

        return new AdapterPaymentLinkResponse(payment);
    }

    @Override
    @Transactional
    public AdaptorWebhookResponse validateAndParseWebhook(Map<String, Object> webhookPayload) {
        log.info("Validating and parsing webhook payload: {}", webhookPayload);

        var paymentStatus = PaymentStatusEnum.valueOf((String) webhookPayload.get("paymentStatus"));
        var paymentIdValue = (Number) webhookPayload.get("paymentId");
        if (paymentIdValue == null) {
            throw new IllegalArgumentException("paymentId is required");
        }
        var paymentId = paymentIdValue.longValue();

        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for paymentId: " + paymentId));

        payment.setStatus(paymentStatus);
        var updatedPayment = paymentRepository.save(payment);

        return new AdaptorWebhookResponse(
                updatedPayment.getStatus(),
                updatedPayment.getId(),
                updatedPayment.getPaymentChannelTxnId(),
                updatedPayment.getMerchantReferencePaymentId());
    }
}
