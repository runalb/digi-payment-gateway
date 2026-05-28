package com.digirestro.digi_payment_gateway.payment.service;

import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.integration.api.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.merchant.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.merchant.service.MerchantService;
import com.digirestro.digi_payment_gateway.payment.contract.PaymentLinkOrchestrationContract;
import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.payment_channel.dto.PaymentLinkStrategyResponse;
import com.digirestro.digi_payment_gateway.payment_channel.interfaces.PaymentChannelStrategy;
import com.digirestro.digi_payment_gateway.payment_channel.resolver.PaymentChannelStrategyResolver;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Orchestrates payment-link creation using a fixed two-phase flow.
 *
 * <p><b>Do not change without team review.</b> Contract is enforced by
 * {@link com.digirestro.digi_payment_gateway.payment.PaymentOrchestrationServiceContractTest}.
 *
 * <ol>
 *   <li>Phase 1 — persist {@link PaymentStatusEnum#INITIATED} (committed before any channel HTTP call)</li>
 *   <li>Phase 2 — {@link #completePaymentLinkGeneration(PaymentEntity)}:
 *       refetch from DB, resolve channel strategy, channel API, persist link fields and
 *       {@link PaymentStatusEnum#PAYMENT_LINK_GENERATED}</li>
 * </ol>
 *
 * <p>Rules:
 * <ul>
 *   <li>Do not add {@code @Transactional} on {@link #generatePaymentLink} — external HTTP must run outside a DB transaction</li>
 *   <li>Do not merge both phases into one transaction</li>
 *   <li>Do not persist payment or set status inside {@link com.digirestro.digi_payment_gateway.payment_channel.interfaces.PaymentChannelStrategy#createPaymentLink}</li>
 *   <li>Always refetch and resolve strategy from persisted payment channel in {@link #completePaymentLinkGeneration}</li>
 * </ul>
 */
@PaymentLinkOrchestrationContract
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
        // Phase 1
        Long merchantId = merchant.getId();

        MerchantPaymentChannelConfigEntity merchantPaymentChannelConfig = merchantService.findPaymentChannelConfigByMerchantId(merchantId);

        MerchantConfigEntity merchantConfig = merchantService.findMerchantConfigByMerchantId(merchantId);

        PaymentEntity payment = new PaymentEntity();
        payment.setMerchant(merchant);
        payment.setMerchantPaymentChannelConfig(merchantPaymentChannelConfig);
        payment.setPaymentChannel(merchantPaymentChannelConfig.getPaymentChannel());
        payment.setCurrency(merchantConfig.getCurrency());
        payment.setAmount(request.amount());
        payment.setMerchantReferencePaymentId(request.merchantReferencePaymentId());
        // payment.setMerchantMetadataJson(request.merchantMetadataJson());
        payment.setRedirectSuccessUrl(resolveRedirectUrl(
                request.redirectSuccessUrl(), merchantConfig.getRedirectSuccessUrl(), "redirectSuccessUrl"));
        payment.setRedirectFailureUrl(resolveRedirectUrl(
                request.redirectFailureUrl(), merchantConfig.getRedirectFailureUrl(), "redirectFailureUrl"));
        payment.setStatus(PaymentStatusEnum.INITIATED);
        payment = paymentService.save(payment);

        // Phase 2
        payment = completePaymentLinkGeneration(payment);

        return new PaymentLinkResponse(
                payment.getId(),
                payment.getPaymentChannelPayLink(),
                payment.getPaymentChannelTxnId(),
                payment.getStatus()
        );
    }

    /**
     * Phase 2 only. Channel HTTP runs here, between two independent commits.
     */
    @PaymentLinkOrchestrationContract
    private PaymentEntity completePaymentLinkGeneration(PaymentEntity payment) {
        PaymentEntity paymentToUpdate = paymentService.findById(payment.getId());

        PaymentChannelStrategy strategy = strategyResolver.getRequiredStrategy(paymentToUpdate.getPaymentChannel().getName());

        PaymentLinkStrategyResponse strategyResponse = strategy.createPaymentLink(paymentToUpdate);

        paymentToUpdate.setPaymentChannelPayLink(strategyResponse.paymentChannelPayLink());
        paymentToUpdate.setPaymentChannelTxnId(strategyResponse.paymentChannelTxnId());
        paymentToUpdate.setStatus(strategyResponse.status());
        paymentToUpdate.setPaymentChannelRawResponseJson(strategyResponse.paymentChannelRawResponseJson());
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
