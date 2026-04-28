package com.runalb.ondemand_service.security;

import java.util.List;

import com.runalb.ondemand_service.role.enums.RoleNameEnum;

/**
 * Parsed access-token claims used by {@link JwtAuthenticationFilter}.
 *
 * @param subjectUserId Parsed from {@code sub} claim.
 * @param roleNames Values from JWT {@code roles} claim, mapped to {@link RoleNameEnum}.
 */
public record JwtPayload(Long subjectUserId, List<RoleNameEnum> roleNames) {}
