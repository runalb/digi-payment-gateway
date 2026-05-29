package com.digirestro.digi_payment_gateway.payment.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.CheckoutRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.CheckoutResponse;
import com.digirestro.digi_payment_gateway.logging.support.HttpExchangeCorrelation;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.service.MerchantService;
import com.digirestro.digi_payment_gateway.payment.contract.CheckoutOrchestrationContract;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentOriginEnum;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentTypeEnum;
import com.digirestro.digi_payment_gateway.payment_channel.dto.CheckoutStrategyResponse;
import com.digirestro.digi_payment_gateway.payment_channel.interfaces.PaymentChannelStrategy;
import com.digirestro.digi_payment_gateway.payment_channel.resolver.PaymentChannelStrategyResolver;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Orchestrates checkout creation using a fixed two-phase flow.
 *
 * <p><b>Do not change without team review.</b> Contract is enforced by
 * {@link com.digirestro.digi_payment_gateway.payment.PaymentOrchestrationServiceContractTest}.
 *
 * <ol>
 *   <li>Phase 1 — persist {@link PaymentStatusEnum#INITIATED} (committed before any channel HTTP call)</li>
 *   <li>Phase 2 — {@link #completeCheckoutGeneration(PaymentEntity)}:
 *       refetch from DB, resolve channel strategy, channel API, persist checkout fields and
 *       {@link PaymentStatusEnum#CHECKOUT_GENERATED}</li>
 * </ol>
 *
 * <p>Rules:
 * <ul>
 *   <li>Do not add {@code @Transactional} on {@link #generateCheckout} — external HTTP must run outside a DB transaction</li>
 *   <li>Do not merge both phases into one transaction</li>
 *   <li>Do not persist payment or set status inside {@link PaymentChannelStrategy#createCheckout}</li>
 *   <li>Always refetch and resolve strategy from persisted payment channel in {@link #completeCheckoutGeneration}</li>
 * </ul>
 */
@CheckoutOrchestrationContract
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

    public CheckoutResponse generateCheckout(
            MerchantEntity merchant, CheckoutRequest request, PaymentOriginEnum paymentOrigin) {
        Objects.requireNonNull(paymentOrigin, "paymentOrigin");

        // Phase 1
        Long merchantId = merchant.getId();

        MerchantPaymentChannelConfigEntity merchantPaymentChannelConfig =
                merchantService.findPaymentChannelConfigByMerchantId(merchantId);

        MerchantConfigEntity merchantConfig = merchantService.findMerchantConfigByMerchantId(merchantId);

        PaymentEntity payment = new PaymentEntity();
        payment.setMerchant(merchant);
        payment.setMerchantPaymentChannelConfig(merchantPaymentChannelConfig);
        payment.setPaymentChannel(merchantPaymentChannelConfig.getPaymentChannel());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setAmount(request.amount());
        payment.setMerchantReferenceId(request.merchantReferenceId());
        payment.setRedirectSuccessUrl(resolveRedirectUrl(
                request.redirectSuccessUrl(), merchantConfig.getRedirectSuccessUrl(), "redirectSuccessUrl"));
        payment.setRedirectFailureUrl(resolveRedirectUrl(
                request.redirectFailureUrl(), merchantConfig.getRedirectFailureUrl(), "redirectFailureUrl"));
        payment.setPaymentType(PaymentTypeEnum.CHECKOUT);
        payment.setPaymentOrigin(paymentOrigin);
        payment.setStatus(PaymentStatusEnum.INITIATED);
        String correlationId = HttpExchangeCorrelation.currentOrNull();
        if (StringUtils.hasText(correlationId)) {
            payment.setHttpExchangeCorrelationId(correlationId.trim());
        }
        payment = paymentService.save(payment);

        // Phase 2
        payment = completeCheckoutUrlGeneration(payment);

        return new CheckoutResponse(
                payment.getId(),
                payment.getPaymentChannelCheckoutUrl(),
                payment.getPaymentChannelTxnId(),
                payment.getStatus());
    }

    /**
     * Phase 2 only. Channel HTTP runs here, between two independent commits.
     */
    @CheckoutOrchestrationContract
    private PaymentEntity completeCheckoutUrlGeneration(PaymentEntity payment) {
        PaymentEntity paymentToUpdate = paymentService.findById(payment.getId());

        PaymentChannelStrategy strategy =
                strategyResolver.getRequiredStrategy(paymentToUpdate.getPaymentChannel().getName());

        CheckoutStrategyResponse strategyResponse = strategy.createCheckout(paymentToUpdate);

        paymentToUpdate.setPaymentChannelCheckoutUrl(strategyResponse.paymentChannelCheckoutUrl());
        paymentToUpdate.setPaymentChannelTxnId(strategyResponse.paymentChannelTxnId());
        paymentToUpdate.setStatus(strategyResponse.status());
        return paymentService.save(paymentToUpdate);
    }

    private static String resolveRedirectUrl(String fromRequest, String fromMerchantConfig, String fieldName) {
        if (StringUtils.hasText(fromRequest)) {
            return fromRequest.trim();
        }
        if (StringUtils.hasText(fromMerchantConfig)) {
            return fromMerchantConfig.trim();
        }
        throw new IllegalArgumentException(
                fieldName + " must be provided in the request or configured on the merchant");
    }
}
