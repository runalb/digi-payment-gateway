package com.digirestro.digi_payment_gateway.payment.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.service.MerchantService;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.PaymentChannelStrategy;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.PaymentChannelStrategyResolver;
import com.digirestro.digi_payment_gateway.paymentchannelstrategy.dto.PaymentLinkStrategyResponse;

import java.util.UUID;

import org.springframework.stereotype.Service;

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

    public PaymentLinkResponse generatePaymentLink(MerchantEntity merchant, PaymentLinkRequest request) {
        Long merchantId = merchant.getId();

        MerchantPaymentChannelConfigEntity merchantPaymentChannelConfig =
                merchantService.findPaymentChannelConfigByMerchantId(merchantId);

        PaymentChannelStrategy strategy = strategyResolver.getRequiredStrategy(
                merchantPaymentChannelConfig.getPaymentChannel().getName());

        MerchantConfigEntity merchantConfig = merchantService.findMerchantConfigByMerchantId(merchantId);

        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentReferenceId(UUID.randomUUID());
        payment.setMerchant(merchant);
        payment.setMerchantPaymentChannelConfig(merchantPaymentChannelConfig);
        payment.setPaymentChannel(merchantPaymentChannelConfig.getPaymentChannel());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setAmount(request.amount());
        payment.setMerchantReferencePaymentId(request.merchantReferencePaymentId());
        payment.setMerchantMetadataJson(request.merchantMetadataJson());
        payment.setStatus(PaymentStatusEnum.INITIATED);
        payment = paymentService.save(payment);

        payment = completePaymentLinkGeneration(payment, strategy);

        return new PaymentLinkResponse(
                payment.getId(),
                payment.getPaymentChannelPayLink(),
                payment.getPaymentChannelTxnId(),
                payment.getStatus()
        );
    }

    private PaymentEntity completePaymentLinkGeneration(PaymentEntity payment, PaymentChannelStrategy strategy) {
        PaymentLinkStrategyResponse strategyResponse = strategy.createPaymentLink(payment);

        PaymentEntity paymentToUpdate = paymentService.findById(payment.getId());
        paymentToUpdate.setPaymentChannelPayLink(strategyResponse.paymentChannelPayLink());
        paymentToUpdate.setPaymentChannelTxnId(strategyResponse.paymentChannelTxnId());
        paymentToUpdate.setStatus(PaymentStatusEnum.PAYMENT_LINK_GENERATED);
        return paymentService.save(paymentToUpdate);
    }
}
