package com.digirestro.digi_payment_gateway.payment.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.service.MerchantService;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.PaymentChannelStrategy;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.PaymentChannelStrategyResolver;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.PaymentLinkStrategyResponse;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentOrchestrationService {

    private final MerchantService merchantService;
    private final PaymentService paymentService;
    private final PaymentChannelStrategyResolver strategyResolver;

    public PaymentOrchestrationService(
            MerchantService merchantService,
            PaymentService paymentService,
            PaymentChannelStrategyResolver strategyResolver) {
        this.merchantService = merchantService;
        this.paymentService = paymentService;
        this.strategyResolver = strategyResolver;
    }

    @Transactional
    public PaymentLinkResponse generatePaymentLink(MerchantEntity merchant, PaymentLinkRequest request) {
        Long merchantId = merchant.getId();

        MerchantPaymentChannelConfigEntity merchantPaymentChannelConfig =
                merchantService.findPaymentChannelConfigByMerchantId(merchantId);

        PaymentChannelStrategy strategy = strategyResolver.requireByChannelName(
                merchantPaymentChannelConfig.getPaymentChannel().getName());
                
        MerchantConfigEntity merchantConfig = merchantService.findMerchantConfigByMerchantId(merchantId);

        PaymentEntity payment = new PaymentEntity();
        UUID paymentReferenceId = UUID.randomUUID();
        payment.setPaymentReferenceId(paymentReferenceId);
        payment.setMerchant(merchant);
        payment.setMerchantPaymentChannelConfig(merchantPaymentChannelConfig);
        payment.setPaymentChannel(merchantPaymentChannelConfig.getPaymentChannel());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setAmount(request.amount());
        payment.setMerchantReferencePaymentId(request.merchantReferencePaymentId());
        payment.setMerchantMetadataJson(request.merchantMetadataJson());
        
        // payment.setDigiPaymentLink(buildDigiPaymentLink(paymentReferenceId));
        payment = paymentService.save(payment);

        PaymentLinkStrategyResponse strategyResponse = strategy.createPaymentLink(payment);
        payment.setPaymentChannelPayLink(strategyResponse.payment().getPaymentChannelPayLink());
        payment.setPaymentChannelTxnId(strategyResponse.payment().getPaymentChannelTxnId());
        payment.setStatus(strategyResponse.payment().getStatus());
        payment = paymentService.save(payment);

        return new PaymentLinkResponse(
                payment.getId(),
                // payment.getDigiPaymentLink(),
                payment.getPaymentChannelPayLink(),
                payment.getPaymentChannelTxnId(),
                payment.getStatus()
        );
    }

    // private String buildDigiPaymentLink(UUID paymentReferenceId) {
    //     return "http://localhost:8080" + "/pay/" + paymentReferenceId.toString();
    // }

    @Transactional(readOnly = true)
    public PaymentDetailsResponse getPaymentDetails(Long paymentId, Long merchantId) {
        PaymentEntity payment = paymentService.findByIdAndMerchantId(paymentId, merchantId);
        return toPaymentDetailsResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailsResponse> listPaymentDetails(Long merchantId) {
        return paymentService.findAllByMerchantIdOrderByCreatedDateTimeDesc(merchantId)
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
                // payment.getDigiPaymentLink(),
                payment.getPaymentChannelPayLink(),
                payment.getCreatedDateTime(),
                payment.getUpdatedDateTime()
        );
    }
}
