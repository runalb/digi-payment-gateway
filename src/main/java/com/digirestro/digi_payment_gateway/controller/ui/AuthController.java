package com.digirestro.digi_payment_gateway.controller.ui;

import com.digirestro.digi_payment_gateway.dto.AuthLoginRequest;
import com.digirestro.digi_payment_gateway.dto.AuthLoginResponse;
import com.digirestro.digi_payment_gateway.dto.AuthLogoutRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileOtpRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileVerifyOtpRequest;
import com.digirestro.digi_payment_gateway.dto.AuthMobileOtpRequestResponse;
import com.digirestro.digi_payment_gateway.dto.AuthRefreshRequest;
import com.digirestro.digi_payment_gateway.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ui/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/login/mobile/request-otp")
    public ResponseEntity<AuthMobileOtpRequestResponse> requestMobileOtp(@Valid @RequestBody AuthMobileOtpRequest request) {
        return ResponseEntity.ok(authService.requestMobileOtp(request));
    }

    @PostMapping("/login/mobile/verify-otp")
    public ResponseEntity<AuthLoginResponse> verifyMobileOtp(
            @Valid @RequestBody AuthMobileVerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyMobileOtp(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthLoginResponse> refresh(@Valid @RequestBody AuthRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthLogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
