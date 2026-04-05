package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.dto.AuthLoginRequest;
import com.digirestro.digi_payment_gateway.dto.AuthLoginResponse;
import com.digirestro.digi_payment_gateway.dto.AuthLogoutRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileOtpRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileVerifyOtpRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileOtpRequestResponse;
import com.digirestro.digi_payment_gateway.dto.AuthRefreshRequest;
import com.digirestro.digi_payment_gateway.entity.RefreshTokenEntity;
import com.digirestro.digi_payment_gateway.entity.UserEntity;
import com.digirestro.digi_payment_gateway.repository.RefreshTokenRepository;
import com.digirestro.digi_payment_gateway.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRATION_SECONDS = 300;
    private final Map<String, OtpSession> otpSessions = new ConcurrentHashMap<>();

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshExpirationSeconds;
    private final long otpResendCooldownSeconds;

    public AuthService(
            UserService userService,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${security.jwt.refresh-expiration-seconds:1209600}") long refreshExpirationSeconds,
            @Value("${security.otp.resend-cooldown-seconds:45}") long otpResendCooldownSeconds) {
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
        this.otpResendCooldownSeconds = otpResendCooldownSeconds;
    }

    @Transactional
    public AuthLoginResponse login(AuthLoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        var user = userService.findActiveUserByEmail(normalizedEmail);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return issueTokens(user);
    }

    @Transactional(readOnly = true)
    public AuthMobileOtpRequestResponse requestMobileOtp(AuthMobileOtpRequest request) {
        String mobileNumber = normalizeMobile(request.mobileNumber());
        userService.findActiveUserByMobile(mobileNumber);

        LocalDateTime now = LocalDateTime.now();
        OtpSession existingSession = otpSessions.get(mobileNumber);
        if (existingSession != null && existingSession.expiresAt().isAfter(now)) {
            LocalDateTime allowedAt = existingSession.requestedAt().plusSeconds(otpResendCooldownSeconds);
            if (allowedAt.isAfter(now)) {
                long retryAfterSeconds = Math.max(1, Duration.between(now, allowedAt).getSeconds());
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "OTP requested too frequently. Try again in " + retryAfterSeconds + " seconds.");
            }
        }

        String otp = generateOtp();
        String otpHash = hashToken(otp);
        LocalDateTime expiresAt = now.plusSeconds(OTP_EXPIRATION_SECONDS);
        otpSessions.put(mobileNumber, new OtpSession(otpHash, now, expiresAt));

        // Placeholder: wire this to an SMS provider integration later.
        log.info("Generated OTP for mobile {}: {}", mobileNumber, otp);
        return new AuthMobileOtpRequestResponse("OTP sent successfully.");
    }

    @Transactional
    public AuthLoginResponse verifyMobileOtp(AuthMobileVerifyOtpRequest request) {
        String mobileNumber = normalizeMobile(request.mobileNumber());
        UserEntity user = userService.findActiveUserByMobile(mobileNumber);

        OtpSession otpSession = otpSessions.get(mobileNumber);
        if (otpSession == null || otpSession.expiresAt().isBefore(LocalDateTime.now())) {
            otpSessions.remove(mobileNumber);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "OTP expired or not requested");
        }

        if (!otpSession.otpHash().equals(hashToken(request.otp().trim()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid mobile OTP");
        }

        otpSessions.remove(mobileNumber);
        return issueTokens(user);
    }

    @Transactional
    public AuthLoginResponse refresh(AuthRefreshRequest request) {
        String hashedToken = hashToken(request.refreshToken().trim());
        RefreshTokenEntity existing = refreshTokenRepository
                .findByTokenHashAndRevokedAtIsNull(hashedToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            existing.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(existing);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        UserEntity user = existing.getUser();
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }

        existing.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(existing);
        return issueTokens(user);
    }

    @Transactional
    public void logout(AuthLogoutRequest request) {
        String hashedToken = hashToken(request.refreshToken().trim());
        refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(hashedToken).ifPresent(token -> {
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    @Scheduled(fixedDelayString = "${security.otp.cleanup-interval-ms:60000}")
    public void removeExpiredOtpSessions() {
        LocalDateTime now = LocalDateTime.now();
        otpSessions.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private AuthLoginResponse issueTokens(UserEntity user) {
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = generateRefreshToken();

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenHash(hashToken(refreshToken));
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationSeconds));
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthLoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpirationSeconds(),
                refreshToken,
                refreshExpirationSeconds);
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hashed) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    private String generateOtp() {
        int max = (int) Math.pow(10, OTP_LENGTH);
        int min = max / 10;
        int otp = min + SECURE_RANDOM.nextInt(max - min);
        return String.valueOf(otp);
    }

    private String normalizeMobile(String mobileNumber) {
        if (!StringUtils.hasText(mobileNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "mobileNumber is required");
        }
        return mobileNumber.trim();
    }

    private record OtpSession(String otpHash, LocalDateTime requestedAt, LocalDateTime expiresAt) {}
}
