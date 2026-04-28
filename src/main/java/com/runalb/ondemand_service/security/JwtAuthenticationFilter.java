package com.runalb.ondemand_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.runalb.ondemand_service.role.enums.RoleNameEnum;

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
        Optional<JwtPayload> payload = jwtService.validateAndParsePayload(token);
        if (payload.isEmpty()) {
            unauthorized(response, "Invalid or expired token");
            return;
        }

        JwtPayload jwtPayload = payload.get();
        Long userId = jwtPayload.subjectUserId();
        List<GrantedAuthority> authorities = toAuthorities(jwtPayload.roleNames());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private static List<GrantedAuthority> toAuthorities(List<RoleNameEnum> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return List.of();
        }
        List<GrantedAuthority> out = new ArrayList<>(roleNames.size());
        for (RoleNameEnum roleName : roleNames) {
            if (roleName == null) {
                continue;
            }
            out.add(new SimpleGrantedAuthority("ROLE_" + roleName.name()));
        }
        return out;
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
        if (HttpMethod.POST.matches(method) && "/api/v1/users".equals(path)) {
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
