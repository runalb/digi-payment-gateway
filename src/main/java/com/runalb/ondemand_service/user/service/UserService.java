package com.runalb.ondemand_service.user.service;

import com.runalb.ondemand_service.merchant.entity.MerchantEntity;
import com.runalb.ondemand_service.role.entity.RoleEntity;
import com.runalb.ondemand_service.role.enums.RoleNameEnum;
import com.runalb.ondemand_service.role.repository.RoleRepository;
import com.runalb.ondemand_service.user.dto.UserCreateRequest;
import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.dto.UserUpdateRequest;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.user.repository.UserRepository;
import com.runalb.ondemand_service.util.StringNormalizer;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Active user with roles loaded; used after JWT resolves principal user id. */
    @Transactional(readOnly = true)
    public UserEntity requireActiveUserWithRoles(Long userId) {
        UserEntity user = userRepository
                .findByIdWithRoles(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated"));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserEntity loadUserWithRolesForTokens(Long userId) {
        UserEntity user = userRepository
                .findByIdWithRoles(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String email = StringNormalizer.normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        String mobileNumber = StringNormalizer.normalizeMobile(request.mobileNumber());
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already registered");
        }

        UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setMobileNumber(mobileNumber);
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.setName(StringNormalizer.normalizeName(request.name()));
        entity.setIsActive(true);
        entity.setIsVerified(false);

        LinkedHashSet<RoleNameEnum> distinctRoleNames = new LinkedHashSet<>(request.roles());
        for (RoleNameEnum roleName : distinctRoleNames) {
            RoleEntity role = roleRepository
                    .findByRoleName(roleName)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Unknown or unavailable role: " + roleName.name()));
            entity.getRoles().add(role);
        }

        UserEntity saved = userRepository.save(entity);
        saved = userRepository.findByIdWithRoles(saved.getId()).orElse(saved);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        UserEntity user = userRepository
                .findByIdWithRoles(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        if (request == null
                || (request.email() == null
                        && request.name() == null
                        && request.mobileNumber() == null
                        && request.password() == null
                        && request.isVerified() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (request.email() != null) {
            String normalizedEmail = StringNormalizer.normalizeEmail(request.email());
            userRepository
                    .findByEmail(normalizedEmail)
                    .filter(other -> !other.getId().equals(userId))
                    .ifPresent(u -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
                    });
            user.setEmail(normalizedEmail);
        }
        if (request.name() != null) {
            String name = StringNormalizer.normalizeName(request.name());
            user.setName(name);
        }
        if (request.mobileNumber() != null) {
            String mobile = StringNormalizer.normalizeMobile(request.mobileNumber());
            if (userRepository.existsByMobileNumber(mobile) && !mobile.equals(user.getMobileNumber())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already registered");
            }
            user.setMobileNumber(mobile);
        }

        if (request.isVerified() != null) {
            user.setIsVerified(request.isVerified());
        }

        user = userRepository.save(user);
        user = userRepository.findByIdWithRoles(userId).orElse(user);
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public boolean userOwnsMerchant(Long userId, Long merchantId) {
        return userRepository.existsByIdAndMerchants_Id(userId, merchantId);
    }

    @Transactional
    public void linkMerchantToUser(Long userId, MerchantEntity merchant) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        user.getMerchants().add(merchant);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserEntity findActiveUserByEmail(String email) {
        UserEntity user =
                userRepository.findByEmailWithRoles(email).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found for email: " + email));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserEntity findActiveUserByMobile(String mobileNumber) {
        UserEntity user = userRepository.findByMobileNumberWithRoles(mobileNumber).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found for mobile number: " + mobileNumber));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
    }

    @Transactional
    public void updatePasswordForActiveUser(String normalizedEmail, String newPassword) {
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 8 || newPassword.length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password must be between 8 and 128 characters");
        }
        UserEntity user = findActiveUserByEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse reactivateUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setIsActive(true);
        user = userRepository.save(user);
        user = userRepository.findByIdWithRoles(userId).orElse(user);
        return toResponse(user);
    }

    private static UserResponse toResponse(UserEntity user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .distinct()
                .sorted()
                .toList();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getName(),
                user.getIsActive(),
                user.getIsVerified(),
                roles);
    }
}
