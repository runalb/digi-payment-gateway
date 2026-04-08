package com.digirestro.digi_payment_gateway.controller.portal;

import com.digirestro.digi_payment_gateway.auth.service.AuthService;
import com.digirestro.digi_payment_gateway.dto.UserCreateRequest;
import com.digirestro.digi_payment_gateway.dto.UserResponse;
import com.digirestro.digi_payment_gateway.dto.UserUpdateRequest;
import com.digirestro.digi_payment_gateway.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portal/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // @GetMapping
    // public ResponseEntity<String> listUsers() {
    //     return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
    //             .body("List users endpoint is not implemented yet.");
    // }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
        authService.assertAuthenticatedUserOwnsUserId(userId);
        UserResponse response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("userId") Long userId, @Valid @RequestBody UserUpdateRequest request) {
        authService.assertAuthenticatedUserOwnsUserId(userId);
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        authService.assertAuthenticatedUserOwnsUserId(userId);
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/reactivate")
    public ResponseEntity<UserResponse> reactivateUser(@PathVariable("userId") Long userId) {
        authService.assertAuthenticatedUserOwnsUserId(userId);
        UserResponse response = userService.reactivateUser(userId);
        return ResponseEntity.ok(response);
    }
}
