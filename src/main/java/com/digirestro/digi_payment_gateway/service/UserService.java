package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.dto.UserCreateRequest;
import com.digirestro.digi_payment_gateway.dto.UserResponse;
import com.digirestro.digi_payment_gateway.dto.UserUpdateRequest;
import com.digirestro.digi_payment_gateway.entity.UserEntity;
import com.digirestro.digi_payment_gateway.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        String mobileNumber = normalizeMobile(request.mobileNumber());
        if (mobileNumber != null && userRepository.existsByMobileNumber(mobileNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setName(request.name().trim());
        user.setIsActive(true);
        user.setIsVerified(false);
        user = userRepository.save(user);

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId)
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
            String email = normalizeEmail(request.email());
            if (!StringUtils.hasText(email)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email must not be blank");
            }
            userRepository
                    .findByEmail(email)
                    .filter(other -> !other.getId().equals(userId))
                    .ifPresent(u -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
                    });
            user.setEmail(email);
        }
        if (request.name() != null) {
            String name = request.name().trim();
            if (!StringUtils.hasText(name)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not be blank");
            }
            user.setName(name);
        }
        if (request.mobileNumber() != null) {
            String mobile = normalizeMobile(request.mobileNumber());
            if (mobile != null
                    && userRepository.existsByMobileNumber(mobile)
                    && !mobile.equals(user.getMobileNumber())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Mobile number already registered");
            }
            user.setMobileNumber(mobile);
        }
        if (request.password() != null) {
            if (!StringUtils.hasText(request.password())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password must not be blank");
            }
            if (request.password().length() < 8 || request.password().length() > 128) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "password must be between 8 and 128 characters");
            }
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        if (request.isVerified() != null) {
            user.setIsVerified(request.isVerified());
        }

        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public void assertAuthenticatedUserOwnsUserId(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof String email) || !StringUtils.hasText(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        String normalized = normalizeEmail(email);
        UserEntity subjectUser = userRepository
                .findByEmail(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!subjectUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to access this resource");
        }
    }

    @Transactional(readOnly = true)
    public UserEntity findActiveUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found for email: " + email));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public UserEntity findActiveUserByMobile(String mobileNumber) {
        UserEntity user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new EntityNotFoundException("User not found for mobile number: " + mobileNumber));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is deleted");
        }
        return user;
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
        return toResponse(user);
    }

    public String normalizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String normalizeMobile(String mobileNumber) {
        if (!StringUtils.hasText(mobileNumber)) {
            return null;
        }
        return mobileNumber.trim();
    }

    private static UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getName(),
                user.getIsActive(),
                user.getIsVerified());
    }
}
