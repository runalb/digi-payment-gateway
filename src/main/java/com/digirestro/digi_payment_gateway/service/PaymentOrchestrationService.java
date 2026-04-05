package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.adapter.PaymentChannelAdapter;
import com.digirestro.digi_payment_gateway.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.repository.MerchantPaymentChannelConfigRepository;
import com.digirestro.digi_payment_gateway.repository.MerchantConfigRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentOrchestrationService {

    private final MerchantConfigRepository merchantConfigRepository;
    private final MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository;
    private final PaymentService paymentService;
    private final List<PaymentChannelAdapter> adapters;

    public PaymentOrchestrationService(
            MerchantConfigRepository merchantConfigRepository,
            MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository,
            PaymentService paymentService,
            List<PaymentChannelAdapter> adapters) {
        this.merchantConfigRepository = merchantConfigRepository;
        this.merchantPaymentChannelConfigRepository = merchantPaymentChannelConfigRepository;
        this.paymentService = paymentService;
        this.adapters = adapters;
    }

    @Transactional
    public PaymentLinkResponse generatePaymentLink(MerchantEntity merchant, PaymentLinkRequest request) {
        Long merchantId = merchant.getId();

        var merchantPaymentChannelConfig = merchantPaymentChannelConfigRepository
                        .findFirstByMerchant_IdAndIsActiveTrue(merchantId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                        "Active merchant payment channel configuration not found"));

        var merchantConfig = merchantConfigRepository
                        .findByMerchant_Id(merchantId)
                        .orElseThrow(() -> new EntityNotFoundException("Merchant configuration not found"));

        var adapter = adapters.stream()
                        .filter(a -> Objects.equals(a.getChannel().getName(), merchantPaymentChannelConfig.getPaymentChannel().getName()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No adapter found for channel"));

        PaymentEntity payment = new PaymentEntity();
        payment.setMerchant(merchant);
        payment.setMerchantPaymentChannelConfig(merchantPaymentChannelConfig);
        payment.setPaymentChannel(merchantPaymentChannelConfig.getPaymentChannel());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setAmount(request.amount());
        payment.setMerchantReferencePaymentId(request.merchantReferencePaymentId());
        payment.setMerchantMetadataJson(request.merchantMetadataJson());
        payment = paymentService.save(payment);

        // adapter will return the payment with the payment link url and payment details
        AdapterPaymentLinkResponse adapterResponse = adapter.createPaymentLink(payment);
        payment.setPaymentLinkUrl(adapterResponse.payment().getPaymentLinkUrl());
        payment.setPaymentChannelTxnId(adapterResponse.payment().getPaymentChannelTxnId());
        payment.setStatus(adapterResponse.payment().getStatus());
        payment = paymentService.save(payment);

        return new PaymentLinkResponse(
                payment.getId(),
                payment.getPaymentLinkUrl(),
                payment.getPaymentChannelTxnId(),
                payment.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(Long paymentId, Long merchantId) {
        PaymentEntity payment = paymentService.findByIdAndMerchant_Id(paymentId, merchantId);
        return toPaymentDetailsResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailsResponse> listPaymentDetails(Long merchantId) {
        return paymentService.findAllByMerchant_IdOrderByCreatedDateTimeDesc(merchantId)
                .stream()
                .map(this::toPaymentDetailsResponse)
                .toList();
    }

    private PaymentDetailsResponse toPaymentDetailsResponse(PaymentEntity payment) {
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
