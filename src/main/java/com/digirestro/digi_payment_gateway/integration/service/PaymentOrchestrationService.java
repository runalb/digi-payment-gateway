package com.digirestro.digi_payment_gateway.integration.service;

import com.digirestro.digi_payment_gateway.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.integration.adapter.PaymentChannelAdapter;
import com.digirestro.digi_payment_gateway.integration.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.integration.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.integration.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.integration.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.service.MerchantService;
import com.digirestro.digi_payment_gateway.service.PaymentService;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentOrchestrationService {

    private final MerchantService merchantService;
    private final PaymentService paymentService;
    private final PaymentChannelAdapterResolver adapterResolver;

    public PaymentOrchestrationService(
            MerchantService merchantService,
            PaymentService paymentService,
            PaymentChannelAdapterResolver adapterResolver) {
        this.merchantService = merchantService;
        this.paymentService = paymentService;
        this.adapterResolver = adapterResolver;
    }

    @Transactional
    public PaymentLinkResponse generatePaymentLink(MerchantEntity merchant, PaymentLinkRequest request) {
        Long merchantId = merchant.getId();

        MerchantPaymentChannelConfigEntity merchantPaymentChannelConfig = merchantService.findActivePaymentChannelConfigByMerchantId(merchantId);
        PaymentChannelAdapter adapter = adapterResolver.requireByChannelName(merchantPaymentChannelConfig.getPaymentChannel().getName());
        MerchantConfigEntity merchantConfig = merchantService.findMerchantConfigByMerchantId(merchantId);

        PaymentEntity payment = new PaymentEntity();
        payment.setMerchant(merchant);
        payment.setMerchantPaymentChannelConfig(merchantPaymentChannelConfig);
        payment.setPaymentChannel(merchantPaymentChannelConfig.getPaymentChannel());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setAmount(request.amount());
        payment.setMerchantReferencePaymentId(request.merchantReferencePaymentId());
        payment.setMerchantMetadataJson(request.merchantMetadataJson());
        payment = paymentService.save(payment);

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
