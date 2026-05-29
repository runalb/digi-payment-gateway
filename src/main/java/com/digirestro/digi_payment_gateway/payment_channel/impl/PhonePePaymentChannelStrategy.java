package com.digirestro.digi_payment_gateway.payment_channel.impl;

import com.digirestro.digi_payment_gateway.payment.entity.PaymentEntity;
import com.digirestro.digi_payment_gateway.payment.enums.PaymentStatusEnum;
import com.digirestro.digi_payment_gateway.payment_channel.enums.PaymentChannelNameEnum;
import com.digirestro.digi_payment_gateway.payment_channel.dto.CheckoutStrategyResponse;
import com.digirestro.digi_payment_gateway.payment_channel.interfaces.PaymentChannelStrategy;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class PhonePePaymentChannelStrategy implements PaymentChannelStrategy {

    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String PAYMENT_FLOW_TYPE = "PG_CHECKOUT";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String clientId;
    private final String clientVersion;
    private final String clientSecret;
    private final String baseUrl;

    public PhonePePaymentChannelStrategy(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${phonepe.tsp.client-id}") String clientId,
            @Value("${phonepe.tsp.client-version}") String clientVersion,
            @Value("${phonepe.tsp.client-secret}") String clientSecret,
            @Value("${phonepe.tsp.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.clientId = clientId;
        this.clientVersion = clientVersion;
        this.clientSecret = clientSecret;
        this.baseUrl = normalizeBaseUrl(baseUrl);
    }

    @Override
    public PaymentChannelNameEnum getChannelName() {
        return PaymentChannelNameEnum.PHONEPE;
    }

    @Override
    public CheckoutStrategyResponse createCheckout(PaymentEntity payment) {
        try {
            String phonepeMerchatMid = extractPhonepeMerchatMid(payment);
            String redirectUrl = requireRedirectSuccessUrl(payment);
            String accessToken = fetchOAuthToken();

            long amountInPaise = payment.getAmount().movePointRight(2).longValue();
            if (amountInPaise < 100) {
                throw new IllegalArgumentException("PhonePe amount must be at least 100 paise (₹1.00)");
            }

            String paymentId = payment.getId().toString();
            String digiMerchantReferenceId = payment.getMerchantReferenceId();

            Map<String, Object> merchantUrls = Map.of("redirectUrl", redirectUrl);
            Map<String, Object> paymentFlow = Map.of(
                    "type", PAYMENT_FLOW_TYPE,
                    "message", "Payment for merchant reference ID: " + digiMerchantReferenceId,
                    "merchantUrls", merchantUrls);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("merchantOrderId", paymentId);
            payload.put("amount", amountInPaise);
            payload.put("paymentFlow", paymentFlow);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, "O-Bearer " + accessToken);
            headers.set("X-Merchant-Id", phonepeMerchatMid);

            String payUrl = baseUrl + "/checkout/v2/pay";
            
            log.info("PhonePe pay request for paymentId={}, merchantReferenceId={}", paymentId, digiMerchantReferenceId);
            log.info("PhonePe pay request headers: {}", headers);
            log.info("PhonePe pay request payload: {}", payload);

            ResponseEntity<Map> payResponse = restTemplate.exchange(
                    payUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(payload, headers),
                    Map.class);

            Map<String, Object> responseBody = payResponse.getBody();
            if (responseBody == null) {
                throw new IllegalStateException("PhonePe pay API returned an empty response body");
            }

            String checkoutRedirectUrl = requireNonBlankString(responseBody, "redirectUrl");
            String orderId = requireNonBlankString(responseBody, "orderId");
            String rawResponseJson = objectMapper.writeValueAsString(responseBody);

            return new CheckoutStrategyResponse(
                    checkoutRedirectUrl,
                    orderId,
                    PaymentStatusEnum.CHECKOUT_URL_GENERATED,
                    rawResponseJson);
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "PhonePe payment link creation failed for paymentId=" + payment.getId(), ex);
        }
    }

    private String fetchOAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_version", clientVersion);
        form.add("client_secret", clientSecret);
        form.add("grant_type", GRANT_TYPE_CLIENT_CREDENTIALS);

        String tokenUrl = baseUrl + "/v1/oauth/token";
        
        log.info("PhonePe OAuth token request to: {}", tokenUrl);
        log.info("PhonePe OAuth token request headers: {}", headers);
        log.info("PhonePe OAuth token request form: {}", form);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                new HttpEntity<>(form, headers),
                new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("PhonePe OAuth token API returned an empty response body");
        }

        Object accessToken = body.get("access_token");
        if (accessToken == null || !StringUtils.hasText(accessToken.toString())) {
            throw new IllegalStateException("PhonePe OAuth response missing access_token");
        }
        return accessToken.toString();
    }

    private String extractPhonepeMerchatMid(PaymentEntity payment) {
        Map<String, Object> merchantConfig = parseMerchantChannelConfig(payment);
        Object phonepeMerchatMid = merchantConfig.get("phonepeMerchatMid");
        if (phonepeMerchatMid == null || !StringUtils.hasText(phonepeMerchatMid.toString())) {
            throw new IllegalArgumentException("phonepeMerchatMid is required in merchant payment channel configJson");
        }
        return phonepeMerchatMid.toString().trim();
    }

    private static String requireRedirectSuccessUrl(PaymentEntity payment) {
        String redirectSuccessUrl = payment.getRedirectSuccessUrl();
        if (!StringUtils.hasText(redirectSuccessUrl)) {
            throw new IllegalArgumentException(
                    "redirectSuccessUrl must be provided in the payment link request or configured on the merchant");
        }
        return redirectSuccessUrl.trim();
    }

    private Map<String, Object> parseMerchantChannelConfig(PaymentEntity payment) {
        String configJson = payment.getMerchantPaymentChannelConfig().getConfigJson();
        if (!StringUtils.hasText(configJson)) {
            throw new IllegalArgumentException("Merchant payment channel configJson is required for PhonePe");
        }
        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid merchant payment channel configJson for PhonePe", ex);
        }
    }

    private static String requireNonBlankString(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null || !StringUtils.hasText(value.toString())) {
            throw new IllegalStateException("PhonePe pay response missing required field: " + key);
        }
        return value.toString();
    }

    private static String normalizeBaseUrl(String url) {
        if (!StringUtils.hasText(url)) {
            throw new IllegalArgumentException("phonepe.tsp.base-url must not be blank");
        }
        return url.trim();
    }
}
