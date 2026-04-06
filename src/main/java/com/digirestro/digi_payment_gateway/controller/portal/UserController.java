package com.digirestro.digi_payment_gateway.controller.portal;

import com.digirestro.digi_payment_gateway.dto.UserCreateRequest;
import com.digirestro.digi_payment_gateway.dto.UserResponse;
import com.digirestro.digi_payment_gateway.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portal/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO:  authenticated user via JWT - Security checks for all endpoints - user is mapped to merchant then only allow to modify user settings else show you are not authorized to access this resource

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<String> listUsers() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("List users endpoint is not implemented yet.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Get user endpoint is not implemented yet.");
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Update user endpoint is not implemented yet.");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}
