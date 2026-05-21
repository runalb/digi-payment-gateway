package com.runalb.ondemand_service.user.service;

import com.runalb.ondemand_service.role.entity.RoleEntity;
import com.runalb.ondemand_service.role.enums.RoleNameEnum;
import com.runalb.ondemand_service.role.repository.RoleRepository;
import com.runalb.ondemand_service.user.dto.UserCreateRequest;
import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.mapper.UserDtoMapper;
import com.runalb.ondemand_service.user.dto.UserUpdateRequest;
import com.runalb.ondemand_service.user.entity.UserEntity;
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }



    
    // Get User data
    @Transactional(readOnly = true)
    public UserEntity findUserById(Long userId) {
        return userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByEmail(String email) {
        return userRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found for email: " + email));
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByMobileNumber(String mobileNumber) {
        return userRepository
                .findByMobileNumberAndIsDeletedFalse(mobileNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found for mobile number: " + mobileNumber));
    }


    
    // Get User data with roles
    @Transactional(readOnly = true)
    public UserEntity findUserByIdWithRoles(Long userId) {
        return userRepository
                .findWithRolesByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }


    @Transactional(readOnly = true)
    public UserEntity findUserByEmailWithRoles(String email) {
        return userRepository
                .findWithRolesByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByMobileNumberWithRoles(String mobileNumber) {
        return userRepository
                .findWithRolesByMobileNumberAndIsDeletedFalse(mobileNumber)
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
        saved = userRepository.findWithRolesByIdAndIsDeletedFalse(saved.getId()).orElse(saved);

        return UserDtoMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        UserEntity user = findUserByIdWithRoles(userId);
        return UserDtoMapper.toResponse(user);
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
        user = userRepository.findWithRolesByIdAndIsDeletedFalse(userId).orElse(user);
        return UserDtoMapper.toResponse(user);
    }

    @Transactional
    public void updatePasswordForUserByEmail(String normalizedEmail, String newPassword) {
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 8 || newPassword.length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password must be between 8 and 128 characters");
        }
        UserEntity user = userRepository
                .findByEmailAndIsDeletedFalse(normalizedEmail)
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
        user = userRepository.findWithRolesByIdAndIsDeletedFalse(userId).orElse(user);
        return UserDtoMapper.toResponse(user);
    }
}
