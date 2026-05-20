package com.runalb.ondemand_service.booking.controller;

import com.runalb.ondemand_service.booking.dto.BookingCreateRequest;
import com.runalb.ondemand_service.booking.dto.BookingResponse;
import com.runalb.ondemand_service.booking.service.BookingService;
import com.runalb.ondemand_service.role.enums.RoleNameEnum;
import com.runalb.ondemand_service.security.AuthorizationService;
import com.runalb.ondemand_service.security.CurrentUserService;
import com.runalb.ondemand_service.user.entity.UserEntity;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CurrentUserService currentUserService;
    private final AuthorizationService authorizationService;

    public BookingController(
            BookingService bookingService,
            CurrentUserService currentUserService,
            AuthorizationService authorizationService) {
        this.bookingService = bookingService;
        this.currentUserService = currentUserService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingCreateRequest request) {
        authorizationService.assertAuthenticatedUserHasRole(RoleNameEnum.CUSTOMER);
        UserEntity user = currentUserService.resolveAuthenticatedUser();
        BookingResponse response = bookingService.createBooking(user, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<BookingResponse>> listBookingsForUser() {
        authorizationService.assertAuthenticatedUserHasRole(RoleNameEnum.CUSTOMER);
        UserEntity user = currentUserService.resolveAuthenticatedUser();
        return ResponseEntity.ok(bookingService.listBookingsForUser(user.getId()));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BookingResponse>> listBookingsForBusiness(@PathVariable Long businessId) {
        authorizationService.assertAuthenticatedUserHasRole(RoleNameEnum.PROVIDER);
        UserEntity provider = currentUserService.resolveAuthenticatedUser();
        return ResponseEntity.ok(bookingService.listBookingsForBusiness(businessId, provider.getId()));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId));
    }
}
