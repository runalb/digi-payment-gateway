package com.runalb.ondemand_service.provider.service;

import com.runalb.ondemand_service.auth.service.AuthService;
import com.runalb.ondemand_service.provider.dto.ProviderCreateRequest;
import com.runalb.ondemand_service.provider.dto.ProviderDetailResponse;
import com.runalb.ondemand_service.provider.dto.ProviderUpdateRequest;
import com.runalb.ondemand_service.provider.entity.ProviderEntity;
import com.runalb.ondemand_service.provider.repository.ProviderRepository;
import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.util.InputSanitizer;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final AuthService authService;

    public ProviderService(
            ProviderRepository providerRepository,
            AuthService authService) {
        this.providerRepository = providerRepository;
        this.authService = authService;
    }

    @Transactional
    public ProviderDetailResponse createProvider(ProviderCreateRequest request) {
        UserEntity user = authService.resolveAuthenticatedUser();
        
        if (providerRepository.existsByUserId(user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Provider already exists for this user");
        }

        ProviderEntity entity = new ProviderEntity();
        entity.setUser(user);
        entity.setBio(InputSanitizer.trimToNull(request.bio()));
        entity.setIsVerified(Boolean.FALSE);
        entity.setAverageRating(0.0);
        entity.setAddress(InputSanitizer.trimToNull(request.address()));
        entity.setProfileCompletionPercentage(computeProfileCompletion(entity));

        ProviderEntity saved = providerRepository.save(entity);
        return toDetailResponse(requireLoaded(saved.getId()));
    }

    @Transactional
    public ProviderDetailResponse updateProvider(Long providerId, ProviderUpdateRequest request) {
        UserEntity user = authService.resolveAuthenticatedUser();

        ProviderEntity entity =
                providerRepository.findById(providerId).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Provider not found"));

        if (!entity.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this provider");
        }

        if (request.bio() != null) {
            entity.setBio(InputSanitizer.trimToNull(request.bio()));
        }
        if (request.address() != null) {
            entity.setAddress(InputSanitizer.trimToNull(request.address()));
        }

        entity.setProfileCompletionPercentage(computeProfileCompletion(entity));
        providerRepository.save(entity);
        return toDetailResponse(requireLoaded(providerId));
    }

    @Transactional(readOnly = true)
    public ProviderDetailResponse getProvider(Long providerId) {
        UserEntity user = authService.resolveAuthenticatedUser();

        ProviderEntity entity = providerRepository
                .findWithUserAndRolesByIdAndIsDeletedFalse(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));

        if (!entity.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to view this provider");
        }

        return toDetailResponse(entity);
    }

    private ProviderEntity requireLoaded(Long id) {
        return providerRepository
                .findWithUserAndRolesByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));
    }

    @Transactional(readOnly = true)
    public boolean providerBelongsToUser(Long userId, Long providerId) {
        return providerRepository.existsByIdAndUserId(providerId, userId);
    }

    @Transactional(readOnly = true)
    public List<ProviderDetailResponse> getAllProviders() {
        return providerRepository.findWithUserAndRolesByIsDeletedFalseOrderByIdAsc().stream()
                .map(ProviderService::toDetailResponse)
                .toList();
    }

    @Transactional
    public void deleteProvider(Long providerId) {
        ProviderEntity entity = providerRepository
                .findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));
        entity.setIsDeleted(Boolean.TRUE);
        providerRepository.save(entity);
    }

    private static ProviderDetailResponse toDetailResponse(ProviderEntity p) {
        UserEntity u = p.getUser();
        List<String> roles = u.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .distinct()
                .sorted()
                .toList();
        UserResponse userResponse = new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getMobileNumber(),
                u.getName(),
                u.getIsVerified(),
                roles);

        return new ProviderDetailResponse(
                p.getId(),
                p.getBio(),
                Boolean.TRUE.equals(p.getIsVerified()),
                p.getAverageRating() != null ? p.getAverageRating() : 0.0,
                p.getProfileCompletionPercentage(),
                p.getAddress(),
                userResponse);
    }

    /**
     * Weighted completion: bio and verification each contribute to the score.
     */
    static int computeProfileCompletion(ProviderEntity p) {
        int score = 0;
        if (InputSanitizer.hasText(p.getBio())) {
            score += 50;
        }
        if (Boolean.TRUE.equals(p.getIsVerified())) {
            score += 50;
        }
        return Math.min(100, score);
    }
}
