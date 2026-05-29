package com.digirestro.digi_payment_gateway.payment_channel_webhook.service;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.service.PaymentService;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.payment_channel.dto.WebhookStrategyResponse;
import com.digirestro.digi_payment_gateway.payment_channel.interfaces.PaymentChannelStrategy;
import com.digirestro.digi_payment_gateway.payment_channel.resolver.PaymentChannelStrategyResolver;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PaymentChannelWebhookOrchestrationService {

    private final PaymentChannelStrategyResolver strategyResolver;
    private final PaymentService paymentService;

    public PaymentChannelWebhookOrchestrationService(
            PaymentChannelStrategyResolver strategyResolver,
            PaymentService paymentService) {
        this.strategyResolver = strategyResolver;
        this.paymentService = paymentService;
    }

    // public WebhookStrategyResponse processWebhook(PaymentChannelNameEnum channelName, Map<String, Object> webhookPayload) {
    //     PaymentChannelStrategy strategy = strategyResolver.getRequiredStrategy(channelName);
    //     WebhookStrategyResponse parsed = strategy.validateAndParseWebhook(webhookPayload);

    //     PaymentEntity paymentToUpdate = paymentService.findById(parsed.paymentId());
    //     paymentToUpdate.setStatus(parsed.status());
    //     if (parsed.paymentChannelTxnId() != null) {
    //         paymentToUpdate.setPaymentChannelTxnId(parsed.paymentChannelTxnId());
    //     }
    //     PaymentEntity payment = paymentService.save(paymentToUpdate);

    //     return new WebhookStrategyResponse(
    //             payment.getStatus(),
    //             payment.getId(),
    //             payment.getPaymentChannelTxnId(),
    //             payment.getMerchantReferenceId());
    // }
}
