package com.digirestro.digi_payment_gateway.service;

import com.digirestro.digi_payment_gateway.dto.UserCreateRequest;
import com.digirestro.digi_payment_gateway.dto.UserResponse;
import com.digirestro.digi_payment_gateway.entity.UserEntity;
import com.digirestro.digi_payment_gateway.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
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
        String email = request.email().trim().toLowerCase();
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

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getName(),
                user.getIsActive(),
                user.getIsVerified());
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

    private String normalizeMobile(String mobileNumber) {
        if (!StringUtils.hasText(mobileNumber)) {
            return null;
        }
        return mobileNumber.trim();
    }
}
