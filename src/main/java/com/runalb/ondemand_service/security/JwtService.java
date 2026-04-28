package com.runalb.ondemand_service.security;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.runalb.ondemand_service.role.enums.RoleNameEnum;

@Service
public class JwtService {
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private final String secret;
    private final long expirationSeconds;
    private final ObjectMapper objectMapper;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.objectMapper = objectMapper;
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    /** Issues an access token with {@code sub} = user id and {@code roles} = logical role names (e.g. CUSTOMER). */
    public String generateToken(Long userId, List<RoleNameEnum> roleNames) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + expirationSeconds;
        ObjectNode root = objectMapper.createObjectNode();
        root.put("sub", userId);
        ArrayNode rolesArr = root.putArray("roles");
        for (RoleNameEnum roleName : roleNames) {
            rolesArr.add(roleName.name());
        }
        root.put("iat", issuedAt);
        root.put("exp", expiresAt);
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(root);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to serialize JWT payload", ex);
        }

        String header = URL_ENCODER.encodeToString(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
        String payload = URL_ENCODER.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = header + "." + payload;
        String signature = sign(signingInput);
        return signingInput + "." + signature;
    }

    /** Validates signature and expiry; returns parsed claims. */
    public Optional<JwtPayload> validateAndParsePayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }

        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature = sign(signingInput);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            return Optional.empty();
        }

        try {
            String payloadJson = new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(payloadJson);
            long exp = root.path("exp").asLong(0L);
            if (exp <= Instant.now().getEpochSecond()) {
                return Optional.empty();
            }
            JsonNode subNode = root.get("sub");
            if (subNode == null || subNode.isNull()) {
                return Optional.empty();
            }
            long userId = subNode.isIntegralNumber() ? subNode.asLong() : Long.parseLong(subNode.asText());

            List<RoleNameEnum> roleNames = new ArrayList<>();
            JsonNode rolesNode = root.get("roles");
            if (rolesNode != null && rolesNode.isArray()) {
                for (JsonNode n : rolesNode) {
                    if (n != null && n.isTextual()) {
                        try {
                            roleNames.add(RoleNameEnum.valueOf(n.asText().trim()));
                        } catch (IllegalArgumentException ex) {
                            return Optional.empty();
                        }
                    }
                }
            }
            return Optional.of(new JwtPayload(userId, roleNames));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private String sign(String input) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] digest = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return URL_ENCODER.encodeToString(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign JWT", ex);
        }
    }

    private boolean constantTimeEquals(String left, String right) {
        byte[] leftBytes = left.getBytes(StandardCharsets.UTF_8);
        byte[] rightBytes = right.getBytes(StandardCharsets.UTF_8);
        if (leftBytes.length != rightBytes.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < leftBytes.length; i++) {
            result |= leftBytes[i] ^ rightBytes[i];
        }
        return result == 0;
    }
}
