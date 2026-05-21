package com.runalb.ondemand_service.booking.service;

import com.runalb.ondemand_service.booking.dto.BookingCreateRequest;
import com.runalb.ondemand_service.booking.dto.BookingResponse;
import com.runalb.ondemand_service.booking.entity.BookingEntity;
import com.runalb.ondemand_service.booking.enums.BookingStatusEnum;
import com.runalb.ondemand_service.booking.repository.BookingRepository;
import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.business.repository.BusinessRepository;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.mapper.CatalogDtoMapper;
import com.runalb.ondemand_service.offering.entity.BusinessOfferingEntity;
import com.runalb.ondemand_service.offering.repository.BusinessOfferingRepository;
import com.runalb.ondemand_service.relationship.service.EntityLinkService;
import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.util.InputSanitizer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BusinessOfferingRepository businessOfferingRepository;
    private final BusinessRepository businessRepository;
    private final EntityLinkService entityLinkService;

    public BookingService(
            BookingRepository bookingRepository,
            BusinessOfferingRepository businessOfferingRepository,
            BusinessRepository businessRepository,
            EntityLinkService entityLinkService) {
        this.bookingRepository = bookingRepository;
        this.businessOfferingRepository = businessOfferingRepository;
        this.businessRepository = businessRepository;
        this.entityLinkService = entityLinkService;
    }

    @Transactional
    public BookingResponse createBooking(UserEntity user, BookingCreateRequest request) {
        if (!request.scheduledAt().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduledAt must be in the future");
        }

        BusinessOfferingEntity offering = businessOfferingRepository
                .findByIdAndIsDeletedFalse(request.businessOfferingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Offering not found"));

        validateBookableOffering(offering);

        BusinessEntity business = offering.getBusiness();
        CatalogServiceEntity catalogService = offering.getCatalogService();

        BookingEntity booking = new BookingEntity();
        booking.setUser(user);
        booking.setBusinessOffering(offering);
        booking.setBusiness(business);
        booking.setCatalogService(catalogService);
        booking.setScheduledAt(request.scheduledAt());
        booking.setNotes(InputSanitizer.trimToNull(request.notes()));
        booking.setStatus(BookingStatusEnum.PENDING);
        booking.setIsDeleted(Boolean.FALSE);

        booking = bookingRepository.save(booking);
        return toBookingResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listBookingsForUser(Long userId) {
        return bookingRepository.findByUser_IdAndIsDeletedFalseOrderByCreatedDateTimeDesc(userId)
                .stream()
                .map(BookingService::toBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> listBookingsForBusiness(Long businessId, Long providerUserId) {
        requireActiveBusiness(businessId);
        if (!entityLinkService.userHasBusinessAccess(providerUserId, businessId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to access this resource");
        }
        return bookingRepository.findByBusiness_IdAndIsDeletedFalseOrderByCreatedDateTimeDesc(businessId).stream()
                .map(BookingService::toBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId) {
        BookingEntity booking = bookingRepository
                .findByIdAndIsDeletedFalse(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return toBookingResponse(booking);
    }

    private void validateBookableOffering(BusinessOfferingEntity offering) {
        if (!Boolean.TRUE.equals(offering.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offering is not active");
        }
        if (!Boolean.TRUE.equals(offering.getIsVerified())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offering is not verified");
        }
        BusinessEntity business = offering.getBusiness();
        if (Boolean.TRUE.equals(business.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business is not available");
        }
        CatalogServiceEntity catalogService = offering.getCatalogService();
        if (Boolean.TRUE.equals(catalogService.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catalog service is not available");
        }
        if (catalogService.getCatalogCategory() != null
                && Boolean.TRUE.equals(catalogService.getCatalogCategory().getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catalog category is not available");
        }
    }

    private BusinessEntity requireActiveBusiness(Long businessId) {
        return businessRepository
                .findByIdAndIsDeletedFalse(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));
    }

    private static BookingResponse toBookingResponse(BookingEntity booking) {
        UserEntity user = booking.getUser();
        BusinessEntity business = booking.getBusiness();
        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getScheduledAt(),
                booking.getNotes(),
                booking.getBusinessOffering().getId(),
                CatalogDtoMapper.toServiceResponse(booking.getCatalogService()),
                toUserResponse(user),
                toBusinessResponse(business),
                booking.getCreatedDateTime(),
                booking.getUpdatedDateTime());
    }

    private static UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getName(),
                user.getIsVerified(),
                List.of());
    }

    private static BusinessResponse toBusinessResponse(BusinessEntity business) {
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getEmail(),
                business.getIsDeleted(),
                business.getBusinessType(),
                business.getDescription(),
                Boolean.TRUE.equals(business.getIsVerified()),
                business.getAverageRating() != null ? business.getAverageRating() : 0.0,
                business.getAddress(),
                business.getMobileNumber());
    }
}
