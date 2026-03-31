package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationResponse;
import com.digirestro.digi_payment_gateway.dto.MerchantChannelConfigCreateRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantChannelConfigResponse;
import com.digirestro.digi_payment_gateway.entity.MerchantPaymentChannelConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantConfigEntity;
import com.digirestro.digi_payment_gateway.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.repository.MerchantPaymentChannelConfigRepository;
import com.digirestro.digi_payment_gateway.repository.MerchantConfigRepository;
import com.digirestro.digi_payment_gateway.repository.MerchantRepository;
import com.digirestro.digi_payment_gateway.repository.PaymentChannelRepository;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MerchantService {
    private static final String DEFAULT_CURRENCY = "USD";

    private final MerchantRepository merchantRepository;
    private final MerchantConfigRepository merchantConfigRepository;
    private final PaymentChannelRepository paymentChannelRepository;
    private final MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository;

    public MerchantService(
            MerchantRepository merchantRepository,
            MerchantConfigRepository merchantConfigRepository,
            PaymentChannelRepository paymentChannelRepository,
            MerchantPaymentChannelConfigRepository merchantPaymentChannelConfigRepository) {
        this.merchantRepository = merchantRepository;
        this.merchantConfigRepository = merchantConfigRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.merchantPaymentChannelConfigRepository = merchantPaymentChannelConfigRepository;
    }

    @Transactional
    public MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request) {
        String currency = StringUtils.hasText(request.currency())
                ? request.currency().trim().toUpperCase()
                : DEFAULT_CURRENCY;
        String webhookUrl = StringUtils.hasText(request.webhookUrl()) ? request.webhookUrl().trim() : null;

        MerchantEntity merchant = new MerchantEntity();
        merchant.setName(request.name().trim());
        merchant.setApiKey(UUID.randomUUID().toString());
        merchant.setIsActive(true);
        merchant = merchantRepository.save(merchant);

        MerchantConfigEntity config = new MerchantConfigEntity();
        config.setMerchant(merchant);
        config.setCurrency(currency);
        config.setWebhookUrl(webhookUrl);
        merchantConfigRepository.save(config);

        return new MerchantRegistrationResponse(
                merchant.getId(),
                merchant.getName(),
                merchant.getApiKey(),
                merchant.getIsActive(),
                currency,
                webhookUrl);
    }

    @Transactional
    public MerchantChannelConfigResponse createMerchantChannelConfig(Long merchantId, MerchantChannelConfigCreateRequest request) {
        var merchant = merchantRepository
                .findById(merchantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Merchant not found"));

        var paymentChannel = paymentChannelRepository
                .findById(request.paymentChannelId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment channel not found: " + request.paymentChannelId()));

        if (merchantPaymentChannelConfigRepository.existsByMerchant_IdAndPaymentChannel_Id(merchantId, paymentChannel.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Merchant already has configuration for this payment channel");
        }

        boolean active = request.isActive() != null ? request.isActive() : Boolean.TRUE;
        String configJson = StringUtils.hasText(request.configJson()) ? request.configJson() : null;

        MerchantPaymentChannelConfigEntity entity = new MerchantPaymentChannelConfigEntity();
        entity.setMerchant(merchant);
        entity.setPaymentChannel(paymentChannel);
        entity.setIsActive(active);
        entity.setConfigJson(configJson);
        entity = merchantPaymentChannelConfigRepository.save(entity);

        return new MerchantChannelConfigResponse(
                entity.getId(),
                merchant.getId(),
                paymentChannel.getId(),
                paymentChannel.getName(),
                entity.getIsActive(),
                entity.getConfigJson());
    }
}
