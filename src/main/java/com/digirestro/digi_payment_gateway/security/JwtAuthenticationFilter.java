package com.digirestro.digi_payment_gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!requiresJwtAuth(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            unauthorized(response, "Missing Bearer token");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        String subject = jwtService.validateAndExtractSubject(token);
        if (!StringUtils.hasText(subject)) {
            unauthorized(response, "Invalid or expired token");
            return;
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                subject,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private boolean requiresJwtAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (HttpMethod.OPTIONS.matches(method)) {
            return false;
        }
        if (!path.startsWith("/api/")) {
            return false;
        }
        if (path.startsWith("/api/v1/integration/")) {
            return false;
        }
        if (path.startsWith("/webhook/")) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/portal/users".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/login".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/login/email/request-otp".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/login/email/verify-otp".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/forgot-password/email/request-otp".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/forgot-password/email/reset-password".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/login/mobile/request-otp".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/login/mobile/verify-otp".equals(path)) {
            return false;
        }
        if (HttpMethod.POST.matches(method) && "/api/v1/auth/refresh-token".equals(path)) {
            return false;
        }
        return !(HttpMethod.POST.matches(method) && "/api/v1/auth/logout".equals(path));
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
