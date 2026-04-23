package com.digirestro.digi_payment_gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.digirestro.digi_payment_gateway.merchant.entity.MerchantEntity;
import com.digirestro.digi_payment_gateway.merchant.repository.MerchantRepository;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private final String integrationPathPrefix;

    private final MerchantRepository merchantRepository;

    public ApiKeyAuthenticationFilter(
            MerchantRepository merchantRepository,
            @Value("${security.integration.path-prefix:/api/v1/integration/}") String integrationPathPrefix) {
        this.merchantRepository = merchantRepository;
        this.integrationPathPrefix = integrationPathPrefix;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!path.startsWith(integrationPathPrefix)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (!StringUtils.hasText(apiKey)) {
            unauthorized(response, "Missing API key");
            return;
        }

        MerchantEntity merchant = merchantRepository.findByApiKey(apiKey.trim()).orElse(null);
        if (merchant == null || !Boolean.TRUE.equals(merchant.getIsActive())) {
            unauthorized(response, "Invalid API key");
            return;
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                merchant,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_INTEGRATION")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
