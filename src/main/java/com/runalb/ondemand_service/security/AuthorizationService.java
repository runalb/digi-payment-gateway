package com.runalb.ondemand_service.security;

import com.runalb.ondemand_service.relationship.service.EntityLinkService;
import com.runalb.ondemand_service.user.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthorizationService {

    private final CurrentUserService currentUserService;
    private final EntityLinkService entityLinkService;

    public AuthorizationService(CurrentUserService currentUserService, EntityLinkService entityLinkService) {
        this.currentUserService = currentUserService;
        this.entityLinkService = entityLinkService;
    }

    @Transactional(readOnly = true)
    public void assertAuthenticatedUserOwnsUserId(Long userId) {
        UserEntity authenticatedUser = currentUserService.resolveAuthenticatedUser();
        if (!authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to access this resource");
        }
    }

    @Transactional(readOnly = true)
    public void assertAuthenticatedUserOwnsBusiness(Long businessId) {
        UserEntity user = currentUserService.resolveAuthenticatedUser();
        if (!entityLinkService.userHasBusinessAccess(user.getId(), businessId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to access this resource");
        }
    }
}
