package com.runalb.ondemand_service.auth.controller;

import com.runalb.ondemand_service.auth.dto.AuthEmailOtpRequest;
import com.runalb.ondemand_service.auth.dto.AuthEmailVerifyOtpRequest;
import com.runalb.ondemand_service.auth.dto.AuthForgotPasswordResetRequest;
import com.runalb.ondemand_service.auth.dto.AuthLoginRequest;
import com.runalb.ondemand_service.auth.dto.AuthLoginResponse;
import com.runalb.ondemand_service.auth.dto.AuthLogoutRequest;
import com.runalb.ondemand_service.auth.dto.AuthMobileOtpRequest;
import com.runalb.ondemand_service.auth.dto.AuthMobileVerifyOtpRequest;
import com.runalb.ondemand_service.auth.dto.AuthOtpRequestResponse;
import com.runalb.ondemand_service.auth.dto.AuthRefreshRequest;
import com.runalb.ondemand_service.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/login/email/request-otp")
    public ResponseEntity<AuthOtpRequestResponse> requestEmailOtp(@Valid @RequestBody AuthEmailOtpRequest request) {
        return ResponseEntity.ok(authService.requestEmailOtp(request));
    }

    @PostMapping("/login/email/verify-otp")
    public ResponseEntity<AuthLoginResponse> verifyEmailOtp(
            @Valid @RequestBody AuthEmailVerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyEmailOtp(request));
    }

    @PostMapping("/forgot-password/email/request-otp")
    public ResponseEntity<AuthOtpRequestResponse> requestForgotPasswordEmailOtp(
            @Valid @RequestBody AuthEmailOtpRequest request) {
        return ResponseEntity.ok(authService.requestForgotPasswordEmailOtp(request));
    }

    @PostMapping("/forgot-password/email/reset-password")
    public ResponseEntity<Void> resetPasswordWithForgotPasswordEmailOtp(
            @Valid @RequestBody AuthForgotPasswordResetRequest request) {
        authService.resetPasswordWithForgotPasswordEmailOtp(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login/mobile/request-otp")
    public ResponseEntity<AuthOtpRequestResponse> requestMobileOtp(@Valid @RequestBody AuthMobileOtpRequest request) {
        return ResponseEntity.ok(authService.requestMobileOtp(request));
    }

    @PostMapping("/login/mobile/verify-otp")
    public ResponseEntity<AuthLoginResponse> verifyMobileOtp(
            @Valid @RequestBody AuthMobileVerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyMobileOtp(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthLoginResponse> refreshToken(@Valid @RequestBody AuthRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthLogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
