package com.runalb.ondemand_service.banner.controller;

import com.runalb.ondemand_service.banner.dto.BannerCreateRequest;
import com.runalb.ondemand_service.banner.dto.BannerResponse;
import com.runalb.ondemand_service.banner.dto.BannerUpdateRequest;
import com.runalb.ondemand_service.banner.service.BannerService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1/banners")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    public ResponseEntity<List<BannerResponse>> listBanners() {
        return ResponseEntity.ok(bannerService.listBanners());
    }

    @GetMapping("/{bannerId}")
    public ResponseEntity<BannerResponse> getBanner(@PathVariable Long bannerId) {
        return ResponseEntity.ok(bannerService.getBanner(bannerId));
    }

    @PostMapping
    public ResponseEntity<BannerResponse> createBanner(@Valid @RequestBody BannerCreateRequest request) {
        BannerResponse body = bannerService.createBanner(request);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @PatchMapping("/{bannerId}")
    public ResponseEntity<BannerResponse> updateBanner(
            @PathVariable Long bannerId, @Valid @RequestBody BannerUpdateRequest request) {
        return ResponseEntity.ok(bannerService.updateBanner(bannerId, request));
    }

    @DeleteMapping("/{bannerId}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long bannerId) {
        bannerService.deleteBanner(bannerId);
        return ResponseEntity.noContent().build();
    }
}
