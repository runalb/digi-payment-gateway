package com.runalb.ondemand_service.provider.service;

import com.runalb.ondemand_service.auth.service.AuthService;
import com.runalb.ondemand_service.provider.dto.ProviderCreateRequest;
import com.runalb.ondemand_service.provider.dto.ProviderDetailResponse;
import com.runalb.ondemand_service.provider.dto.ProviderUpdateRequest;
import com.runalb.ondemand_service.provider.entity.ProviderEntity;
import com.runalb.ondemand_service.provider.repository.ProviderRepository;
import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.user.repository.UserRepository;
import com.runalb.ondemand_service.util.StringNormalizer;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public ProviderService(
            ProviderRepository providerRepository,
            UserRepository userRepository,
            AuthService authService) {
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional
    public ProviderDetailResponse createProfile(ProviderCreateRequest request) {
        UserEntity user = authService.loadAuthenticatedActiveUser();
        UserEntity managedUser = userRepository
                .findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (providerRepository.existsByUser_Id(managedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Provider profile already exists for this user");
        }

        ProviderEntity entity = new ProviderEntity();
        entity.setUser(managedUser);
        entity.setBio(StringNormalizer.trimToNull(request.bio()));
        entity.setIsVerified(Boolean.FALSE);
        entity.setAverageRating(0.0);
        entity.setIsActive(Boolean.TRUE);
        entity.setAddress(StringNormalizer.trimToNull(request.address()));
        entity.setProfileCompletionPercentage(computeProfileCompletion(entity));

        ProviderEntity saved = providerRepository.save(entity);
        return toDetailResponse(requireLoaded(saved.getId()));
    }

    @Transactional
    public ProviderDetailResponse updateProfile(Long providerId, ProviderUpdateRequest request) {
        Long userId = authService.loadAuthenticatedActiveUser().getId();

        ProviderEntity entity =
                providerRepository.findById(providerId).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Provider profile not found"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this profile");
        }

        if (request.bio() != null) {
            entity.setBio(StringNormalizer.trimToNull(request.bio()));
        }
        if (request.address() != null) {
            entity.setAddress(StringNormalizer.trimToNull(request.address()));
        }
        if (request.isActive() != null) {
            entity.setIsActive(request.isActive());
        }

        entity.setProfileCompletionPercentage(computeProfileCompletion(entity));
        providerRepository.save(entity);
        return toDetailResponse(requireLoaded(providerId));
    }

    @Transactional(readOnly = true)
    public ProviderDetailResponse getProfileForOwner(Long providerId) {
        Long userId = authService.loadAuthenticatedActiveUser().getId();

        ProviderEntity entity = providerRepository
                .findByIdWithUserAndRoles(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider profile not found"));

        if (!entity.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to view this profile");
        }

        return toDetailResponse(entity);
    }

    private ProviderEntity requireLoaded(Long id) {
        return providerRepository
                .findByIdWithUserAndRoles(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider profile not found"));
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
                u.getIsActive(),
                u.getIsVerified(),
                roles);

        return new ProviderDetailResponse(
                p.getId(),
                p.getBio(),
                Boolean.TRUE.equals(p.getIsVerified()),
                p.getAverageRating() != null ? p.getAverageRating() : 0.0,
                p.getProfileCompletionPercentage(),
                Boolean.TRUE.equals(p.getIsActive()),
                p.getAddress(),
                userResponse);
    }

    /**
     * Weighted completion: bio and verification each contribute to the score.
     */
    static int computeProfileCompletion(ProviderEntity p) {
        int score = 0;
        if (StringNormalizer.hasText(p.getBio())) {
            score += 50;
        }
        if (Boolean.TRUE.equals(p.getIsVerified())) {
            score += 50;
        }
        return Math.min(100, score);
    }
}
