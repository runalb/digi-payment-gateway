package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.adapter.PaymentChannelAdapter;
import com.digirestro.digi_payment_gateway.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.repository.MerchantChannelConfigRepository;
import com.digirestro.digi_payment_gateway.repository.MerchantConfigRepository;
import com.digirestro.digi_payment_gateway.repository.MerchantRepository;
import com.digirestro.digi_payment_gateway.repository.PaymentRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentOrchestrationService {

    private final MerchantRepository merchantRepository;
    private final MerchantConfigRepository merchantConfigRepository;
    private final MerchantChannelConfigRepository merchantChannelConfigRepository;
    private final PaymentRepository paymentRepository;
    private final List<PaymentChannelAdapter> adapters;

    public PaymentOrchestrationService(
            MerchantRepository merchantRepository,
            MerchantConfigRepository merchantConfigRepository,
            MerchantChannelConfigRepository merchantChannelConfigRepository,
            PaymentRepository paymentRepository,
            List<PaymentChannelAdapter> adapters) {
        this.merchantRepository = merchantRepository;
        this.merchantConfigRepository = merchantConfigRepository;
        this.merchantChannelConfigRepository = merchantChannelConfigRepository;
        this.paymentRepository = paymentRepository;
        this.adapters = adapters;
    }

    @Transactional
    public PaymentLinkResponse generatePaymentLink(PaymentLinkRequest request) {
        var merchant = merchantRepository.findById(request.merchantId())
                        .orElseThrow(() -> new EntityNotFoundException("Merchant not found"));

        var merchantChannelConfig = merchantChannelConfigRepository
                        .findFirstByMerchant_IdAndIsActiveTrue(request.merchantId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                        "Active merchant channel configuration not found"));

        var merchantConfig = merchantConfigRepository
                        .findByMerchant_Id(request.merchantId())
                        .orElseThrow(() -> new EntityNotFoundException("Merchant configuration not found"));

        var adapter = adapters.stream()
                        .filter(a -> Objects.equals(a.getChannel().getName(), merchantChannelConfig.getPaymentChannel().getName()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No adapter found for channel"));

        PaymentEntity payment = new PaymentEntity();
        payment.setMerchant(merchant);
        payment.setMerchantChannelConfig(merchantChannelConfig);
        payment.setPaymentChannel(merchantChannelConfig.getPaymentChannel());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setAmount(request.amount());
        payment.setMerchantReferencePaymentId(request.merchantReferencePaymentId());
        payment.setMerchantMetadataJson(request.merchantMetadataJson());
        payment = paymentRepository.save(payment);

        // adapter will return the payment with the payment link url and payment details
        AdapterPaymentLinkResponse adapterResponse = adapter.createPaymentLink(payment);
        payment.setPaymentLinkUrl(adapterResponse.payment().getPaymentLinkUrl());
        payment.setPaymentChannelTxnId(adapterResponse.payment().getPaymentChannelTxnId());
        payment.setStatus(adapterResponse.payment().getStatus());
        payment = paymentRepository.save(payment);

        return new PaymentLinkResponse(
                payment.getId(),
                payment.getPaymentLinkUrl(),
                payment.getPaymentChannelTxnId(),
                payment.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(Long paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return new PaymentDetailsResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getMerchant().getId(),
                payment.getMerchantReferencePaymentId(),
                payment.getMerchantMetadataJson(),
                payment.getPaymentChannel().getId(),
                payment.getPaymentChannel().getName(),
                payment.getPaymentChannelTxnId(),
                payment.getPaymentLinkUrl(),
                payment.getCreatedDateTime(),
                payment.getUpdatedDateTime()
        );
    }
}
