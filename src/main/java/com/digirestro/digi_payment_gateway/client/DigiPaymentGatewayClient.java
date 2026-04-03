package com.digirestro.digi_payment_gateway.client;

import com.digirestro.digi_payment_gateway.dto.AuthLoginRequest;
import com.digirestro.digi_payment_gateway.dto.AuthLoginResponse;
import com.digirestro.digi_payment_gateway.dto.AuthLogoutRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileOtpRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileVerifyOtpRequest;
import com.digirestro.digi_payment_gateway.dto.AuthOtpRequestResponse;
import com.digirestro.digi_payment_gateway.dto.AuthRefreshRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantPaymentChannelConfigCreateRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantPaymentChannelConfigResponse;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationRequest;
import com.digirestro.digi_payment_gateway.dto.MerchantRegistrationResponse;
import com.digirestro.digi_payment_gateway.dto.PaymentDetailsResponse;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkRequest;
import com.digirestro.digi_payment_gateway.dto.PaymentLinkResponse;
import com.digirestro.digi_payment_gateway.dto.UserCreateRequest;
import com.digirestro.digi_payment_gateway.dto.UserResponse;
import com.digirestro.digi_payment_gateway.dto.adaptor.AdaptorWebhookResponse;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

/**
 * Typed HTTP client for digi-payment-gateway REST APIs. Mirrors
 * {@code postman/Digi-Payment-Gateway.postman_collection.json} / controllers under {@code controller.*}.
 *
 * <p>Construct with {@link RestClient.Builder} (Spring Boot supplies a builder bean) and your gateway base URL
 * (e.g. {@code http://localhost:8080}). Integration calls require {@code X-API-Key}; UI calls use
 * {@code Authorization: Bearer ...} except auth and user registration endpoints.
 */
public final class DigiPaymentGatewayClient {

    private static final ParameterizedTypeReference<List<PaymentDetailsResponse>> PAYMENT_DETAILS_LIST =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_STRING_OBJECT =
            new ParameterizedTypeReference<>() {};

    private final RestClient client;

    public DigiPaymentGatewayClient(String baseUrl, RestClient.Builder restClientBuilder) {
        this.client = restClientBuilder.baseUrl(baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl).build();
    }

    /** Same as {@link #DigiPaymentGatewayClient(String, RestClient.Builder)} using {@link RestClient#builder()}. */
    public static DigiPaymentGatewayClient create(String baseUrl) {
        return new DigiPaymentGatewayClient(baseUrl, RestClient.builder());
    }

    // --- Integration API (X-API-Key) ---

    public PaymentLinkResponse generatePaymentLink(String apiKey, PaymentLinkRequest request) {
        return client.post()
                .uri("/api/v1/integration/payment-link/generate")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("X-API-Key", apiKey)
                .body(request)
                .retrieve()
                .body(PaymentLinkResponse.class);
    }

    public List<PaymentDetailsResponse> listTransactions(String apiKey) {
        return client.get()
                .uri("/api/v1/integration/transactions")
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(PAYMENT_DETAILS_LIST);
    }

    public PaymentDetailsResponse getPaymentDetails(String apiKey, long paymentId) {
        return client.get()
                .uri("/api/v1/integration/transactions/{id}", paymentId)
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(PaymentDetailsResponse.class);
    }

    public String terminalPaymentTest(String apiKey) {
        return client.get()
                .uri("/api/v1/integration/terminal-payment/test")
                .header("X-API-Key", apiKey)
                .retrieve()
                .body(String.class);
    }

    // --- Webhooks (no auth) ---

    public AdaptorWebhookResponse receiveTestWebhook(Map<String, Object> payload) {
        return client.post()
                .uri("/webhook/v1/payment-channel-webhooks/test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .retrieve()
                .body(AdaptorWebhookResponse.class);
    }

    // --- UI auth (no Bearer) ---

    public AuthLoginResponse login(AuthLoginRequest request) {
        return client.post()
                .uri("/api/v1/ui/auth/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(AuthLoginResponse.class);
    }

    public AuthOtpRequestResponse requestMobileOtp(AuthMobileOtpRequest request) {
        return client.post()
                .uri("/api/v1/ui/auth/login/mobile/request-otp")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(AuthOtpRequestResponse.class);
    }

    public AuthLoginResponse verifyMobileOtp(AuthMobileVerifyOtpRequest request) {
        return client.post()
                .uri("/api/v1/ui/auth/login/mobile/verify-otp")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(AuthLoginResponse.class);
    }

    public AuthLoginResponse refresh(AuthRefreshRequest request) {
        return client.post()
                .uri("/api/v1/ui/auth/refresh")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(AuthLoginResponse.class);
    }

    public void logout(AuthLogoutRequest request) {
        client.post()
                .uri("/api/v1/ui/auth/logout")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    // --- UI merchants & configs (Bearer) ---

    public MerchantRegistrationResponse createMerchant(String bearerAccessToken, MerchantRegistrationRequest request) {
        return client.post()
                .uri("/api/v1/ui/merchants")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .body(request)
                .retrieve()
                .body(MerchantRegistrationResponse.class);
    }

    public String listMerchants(String bearerAccessToken) {
        return client.get()
                .uri("/api/v1/ui/merchants")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .body(String.class);
    }

    public String getMerchant(String bearerAccessToken, long merchantId) {
        return client.get()
                .uri("/api/v1/ui/merchants/{merchantId}", merchantId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .body(String.class);
    }

    public String updateMerchant(String bearerAccessToken, long merchantId, Map<String, Object> body) {
        return client.patch()
                .uri("/api/v1/ui/merchants/{merchantId}", merchantId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    public void deactivateMerchant(String bearerAccessToken, long merchantId) {
        client.delete()
                .uri("/api/v1/ui/merchants/{merchantId}", merchantId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public MerchantPaymentChannelConfigResponse createPaymentChannelConfig(
            String bearerAccessToken, long merchantId, MerchantPaymentChannelConfigCreateRequest request) {
        return client.post()
                .uri("/api/v1/ui/merchants/{merchantId}/payment-channel-configs", merchantId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .body(request)
                .retrieve()
                .body(MerchantPaymentChannelConfigResponse.class);
    }

    public String listPaymentChannelConfigs(String bearerAccessToken, long merchantId) {
        return client.get()
                .uri("/api/v1/ui/merchants/{merchantId}/payment-channel-configs", merchantId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .body(String.class);
    }

    public String getPaymentChannelConfig(String bearerAccessToken, long merchantId, long configId) {
        return client.get()
                .uri("/api/v1/ui/merchants/{merchantId}/payment-channel-configs/{configId}", merchantId, configId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .body(String.class);
    }

    public String updatePaymentChannelConfig(
            String bearerAccessToken, long merchantId, long configId, Map<String, Object> body) {
        return client.patch()
                .uri("/api/v1/ui/merchants/{merchantId}/payment-channel-configs/{configId}", merchantId, configId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    public void deactivatePaymentChannelConfig(String bearerAccessToken, long merchantId, long configId) {
        client.delete()
                .uri("/api/v1/ui/merchants/{merchantId}/payment-channel-configs/{configId}", merchantId, configId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .toBodilessEntity();
    }

    // --- UI users ---

    public UserResponse createUser(UserCreateRequest request) {
        return client.post()
                .uri("/api/v1/ui/users")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .body(UserResponse.class);
    }

    public String listUsers(String bearerAccessToken) {
        return client.get()
                .uri("/api/v1/ui/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .body(String.class);
    }

    public String getUser(String bearerAccessToken, long userId) {
        return client.get()
                .uri("/api/v1/ui/users/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .body(String.class);
    }

    public String updateUser(String bearerAccessToken, long userId, Map<String, Object> body) {
        return client.patch()
                .uri("/api/v1/ui/users/{userId}", userId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .body(body)
                .retrieve()
                .body(String.class);
    }

    public void deactivateUser(String bearerAccessToken, long userId) {
        client.delete()
                .uri("/api/v1/ui/users/{userId}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .toBodilessEntity();
    }

    public String uiTest(String bearerAccessToken) {
        return client.get()
                .uri("/api/v1/ui/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerAccessToken)
                .retrieve()
                .body(String.class);
    }

    // --- Actuator ---

    public Map<String, Object> actuatorHealth() {
        return client.get()
                .uri("/actuator/health")
                .retrieve()
                .body(MAP_STRING_OBJECT);
    }
}
