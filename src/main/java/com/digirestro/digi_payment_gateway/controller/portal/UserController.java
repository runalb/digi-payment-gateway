package com.digirestro.digi_payment_gateway.controller.portal;

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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    
    // open api - no authentication required
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
    
    // TODO:  authenticated user via JWT - Security checks for all endpoints - request user is mapped to user then only allow to modify user settings else show you are not authorized to access this resource
    // add jwt authentication to this endpoint
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("userId") Long userId) {
        UserResponse response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    // add jwt authentication to this endpoint
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("userId") Long userId, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    // add jwt authentication to this endpoint
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    // add jwt authentication to this endpoint
    @PostMapping("/{userId}/reactivate")
    public ResponseEntity<UserResponse> reactivateUser(@PathVariable("userId") Long userId) {
        UserResponse response = userService.reactivateUser(userId);
        return ResponseEntity.ok(response);
    }
}
