package com.runalb.ondemand_service.banner.service;

import com.runalb.ondemand_service.banner.dto.BannerCreateRequest;
import com.runalb.ondemand_service.banner.dto.BannerResponse;
import com.runalb.ondemand_service.banner.dto.BannerUpdateRequest;
import com.runalb.ondemand_service.banner.entity.BannerEntity;
import com.runalb.ondemand_service.banner.mapper.BannerDtoMapper;
import com.runalb.ondemand_service.banner.repository.BannerRepository;
import com.runalb.ondemand_service.util.ImageUrlValidator;
import com.runalb.ondemand_service.util.InputSanitizer;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Transactional(readOnly = true)
    public List<BannerResponse> listBanners() {
        return bannerRepository.findByIsDeletedFalse(Sort.by("displayOrder", "id")).stream()
                .map(BannerDtoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BannerResponse getBanner(Long id) {
        return BannerDtoMapper.toResponse(requireNonDeletedBanner(id));
    }

    @Transactional
    public BannerResponse createBanner(BannerCreateRequest request) {
        BannerEntity e = new BannerEntity();
        e.setTitle(InputSanitizer.trimToNull(request.title()));
        e.setImageUrl(requireImageUrl(request.imageUrl()));
        e.setDisplayOrder(displayOrderVal(request.displayOrder()));
        e = bannerRepository.save(e);
        return BannerDtoMapper.toResponse(e);
    }

    @Transactional
    public BannerResponse updateBanner(Long id, BannerUpdateRequest request) {
        if (request.title() == null
                && request.imageUrl() == null
                && request.displayOrder() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        BannerEntity e = requireBanner(id);
        if (request.title() != null) {
            e.setTitle(InputSanitizer.trimToNull(request.title()));
        }
        if (request.imageUrl() != null) {
            e.setImageUrl(requireImageUrl(request.imageUrl()));
        }
        if (request.displayOrder() != null) {
            e.setDisplayOrder(request.displayOrder());
        }
        e = bannerRepository.save(e);
        return BannerDtoMapper.toResponse(e);
    }

    @Transactional
    public void deleteBanner(Long id) {
        BannerEntity e = requireBanner(id);
        e.setIsDeleted(Boolean.TRUE);
        bannerRepository.save(e);
    }

    private BannerEntity requireBanner(Long id) {
        return bannerRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Banner not found"));
    }

    private BannerEntity requireNonDeletedBanner(Long id) {
        return bannerRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Banner not found"));
    }

    private String requireImageUrl(String value) {
        String url = ImageUrlValidator.normalizeImageUrl(value);
        if (url == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "imageUrl is required");
        }
        return url;
    }

    private int displayOrderVal(Integer v) {
        return v == null ? 0 : v;
    }
}
