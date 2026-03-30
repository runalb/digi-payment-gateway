package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.adapter.PaymentChannelAdapter;
import com.digirestro.digi_payment_gateway.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdapterPaymentLinkResponse;
import com.digirestro.digi_payment_gateway.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.enums.PaymentStatusEnum;
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
    private final MerchantChannelConfigRepository channelConfigRepository;
    private final PaymentRepository paymentRepository;
    private final List<PaymentChannelAdapter> adapters;

    public PaymentOrchestrationService(
            MerchantRepository merchantRepository,
            MerchantConfigRepository merchantConfigRepository,
            MerchantChannelConfigRepository channelConfigRepository,
            PaymentRepository paymentRepository,
            List<PaymentChannelAdapter> adapters) {
        this.merchantRepository = merchantRepository;
        this.merchantConfigRepository = merchantConfigRepository;
        this.channelConfigRepository = channelConfigRepository;
        this.paymentRepository = paymentRepository;
        this.adapters = adapters;
    }

    @Transactional
    public PaymentLinkResponse generatePaymentLink(PaymentLinkRequest request) {
        var merchant = merchantRepository.findById(request.merchantId())
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found"));

        var merchantConfig = merchantConfigRepository
                .findByMerchant_Id(request.merchantId())
                .orElseThrow(() -> new EntityNotFoundException("Merchant configuration not found"));

        var channelConfig = channelConfigRepository.findFirstByMerchant_IdAndIsActiveTrue(request.merchantId())
                .orElseThrow(() -> new EntityNotFoundException("Active merchant channel configuration not found"));

        var adapter = adapters.stream()
                .filter(a -> Objects.equals(a.getChannel().getName(), channelConfig.getPaymentChannel().getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No adapter found for channel"));

        PaymentEntity payment = new PaymentEntity();
        payment.setMerchant(merchant);
        payment.setChannelConfig(channelConfig);
        payment.setPaymentChannel(channelConfig.getPaymentChannel());
        payment.setMerchantReferencePaymentId(request.merchantReferencePaymentId());
        payment.setAmount(request.amount());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setMerchantMetadataJson(request.merchantMetadataJson());
        payment = paymentRepository.save(payment);

        AdapterPaymentLinkResponse adapterResponse = adapter.createPaymentLink(payment, merchantConfig,channelConfig);
        payment.setPaymentLinkUrl(adapterResponse.paymentLinkUrl());
        payment.setPaymentChannelTxnId(adapterResponse.paymentChannelTxnId());
        payment.setStatus(PaymentStatusEnum.PAYMENT_LINK_GENERATED);
        payment = paymentRepository.save(payment);

        return new PaymentLinkResponse(
                payment.getId(),
                payment.getPaymentLinkUrl(),
                payment.getPaymentChannelTxnId(),
                payment.getStatus());
    }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(Long paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return new PaymentDetailsResponse(
                payment.getId(),
                payment.getMerchant().getId(),
                payment.getChannelConfig().getId(),
                payment.getPaymentChannel().getId(),
                payment.getPaymentChannel().getName(),
                payment.getMerchantReferencePaymentId(),
                payment.getPaymentChannelTxnId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getPaymentLinkUrl(),
                payment.getMerchantMetadataJson(),
                payment.getCreatedDateTime(),
                payment.getUpdatedDateTime());
    }
}
