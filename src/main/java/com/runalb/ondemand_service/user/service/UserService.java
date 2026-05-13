package com.runalb.ondemand_service.user.service;

import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.role.entity.RoleEntity;
import com.runalb.ondemand_service.role.enums.RoleNameEnum;
import com.runalb.ondemand_service.role.repository.RoleRepository;
import com.runalb.ondemand_service.user.dto.UserCreateRequest;
import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.dto.UserUpdateRequest;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.provider.repository.ProviderRepository;
import com.runalb.ondemand_service.user.repository.UserRepository;
import com.runalb.ondemand_service.util.InputSanitizer;
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
    private final ProviderRepository providerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            ProviderRepository providerRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }



    
    // Get User data
    @Transactional(readOnly = true)
    public UserEntity findUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted for id: " + userId);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found for email: " + email));
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByMobileNumber(String mobileNumber) {
        UserEntity user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found for mobile number: " + mobileNumber));
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
    }


    
    // Get User data with roles
    @Transactional(readOnly = true)
    public UserEntity findUserByIdWithRoles(Long userId) {
        return userRepository
                .findByIdWithRoles(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }


    @Transactional(readOnly = true)
    public UserEntity findUserByEmailWithRoles(String email) {
        return userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByMobileNumberWithRoles(String mobileNumber) {
        return userRepository.findByMobileNumberWithRoles(mobileNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }





    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String email = InputSanitizer.normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        String mobileNumber = InputSanitizer.normalizeMobile(request.mobileNumber());
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already registered");
        }

        UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setMobileNumber(mobileNumber);
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.setName(InputSanitizer.normalizeName(request.name()));
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
        UserEntity user = findUserByIdWithRoles(userId);
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
            String normalizedEmail = InputSanitizer.normalizeEmail(request.email());
            userRepository
                    .findByEmail(normalizedEmail)
                    .filter(other -> !other.getId().equals(userId))
                    .ifPresent(u -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
                    });
            user.setEmail(normalizedEmail);
        }
        if (request.name() != null) {
            String name = InputSanitizer.normalizeName(request.name());
            user.setName(name);
        }
        if (request.mobileNumber() != null) {
            String mobile = InputSanitizer.normalizeMobile(request.mobileNumber());
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

    // Businesses
    @Transactional(readOnly = true)
    public boolean userOwnsBusiness(Long userId, Long businessId) {
        return userRepository.existsByIdAndBusinesses_Id(userId, businessId);
    }

    @Transactional
    public void linkUserToBusiness(Long userId, BusinessEntity business) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        user.getBusinesses().add(business);
        userRepository.save(user);
    }


    // Providers
    @Transactional(readOnly = true)
    public boolean userOwnsProvider(Long userId, Long providerId) {
        return providerRepository.existsByIdAndUser_Id(providerId, userId);
    }


    



    @Transactional
    public void updatePasswordForUserByEmail(String normalizedEmail, String newPassword) {
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 8 || newPassword.length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password must be between 8 and 128 characters");
        }
        UserEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found for email: " + normalizedEmail));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setIsDeleted(Boolean.TRUE);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse reactivateUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setIsDeleted(Boolean.FALSE);
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
                user.getIsVerified(),
                roles);
    }
}
