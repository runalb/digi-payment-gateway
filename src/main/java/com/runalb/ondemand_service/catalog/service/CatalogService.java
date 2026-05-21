package com.runalb.ondemand_service.catalog.service;

import com.runalb.ondemand_service.catalog.dto.CatalogCategoryCreateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryUpdateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceCreateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.mapper.CatalogDtoMapper;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceUpdateRequest;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceImageEntity;
import com.runalb.ondemand_service.catalog.repository.CatalogCategoryRepository;
import com.runalb.ondemand_service.catalog.repository.CatalogServiceRepository;
import com.runalb.ondemand_service.util.ImageUrlValidator;
import com.runalb.ondemand_service.util.InputSanitizer;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogService {

    private static final String ALREADY_EXISTS_MESSAGE = "Name already exists";

    private final CatalogCategoryRepository categoryRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final int maxServiceImages;

    public CatalogService(
            CatalogCategoryRepository categoryRepository,
            CatalogServiceRepository catalogServiceRepository,
            @Value("${catalog.service.max-images:5}") int maxServiceImages) {
        this.categoryRepository = categoryRepository;
        this.catalogServiceRepository = catalogServiceRepository;
        this.maxServiceImages = maxServiceImages;
    }

    @Transactional(readOnly = true)
    public List<CatalogCategoryResponse> listCategories() {
        return categoryRepository.findByIsDeletedFalse(Sort.by("displayOrder", "id")).stream()
                .map(CatalogDtoMapper::toCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogCategoryResponse getCategory(Long id) {
        return CatalogDtoMapper.toCategoryResponse(requireNonDeletedCategory(id));
    }

    @Transactional
    public CatalogCategoryResponse createCategory(CatalogCategoryCreateRequest request) {
        String name = request.name().trim();
        assertCategoryNameUnique(name, null);
        CatalogCategoryEntity e = new CatalogCategoryEntity();
        e.setName(name);
        e.setDescription(InputSanitizer.trimToNull(request.description()));
        e.setImageUrl(ImageUrlValidator.normalizeImageUrl(request.imageUrl()));
        e.setDisplayOrder(displayOrderVal(request.displayOrder()));
        e = categoryRepository.save(e);
        return CatalogDtoMapper.toCategoryResponse(e);
    }

    @Transactional
    public CatalogCategoryResponse updateCategory(Long id, CatalogCategoryUpdateRequest request) {
        if (request.name() == null
                && request.description() == null
                && request.imageUrl() == null
                && request.displayOrder() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        CatalogCategoryEntity e = requireCategory(id);
        if (request.name() != null) {
            String name = request.name().trim();
            assertCategoryNameUnique(name, id);
            e.setName(name);
        }
        if (request.description() != null) {
            e.setDescription(InputSanitizer.trimToNull(request.description()));
        }
        if (request.imageUrl() != null) {
            e.setImageUrl(ImageUrlValidator.normalizeImageUrl(request.imageUrl()));
        }
        if (request.displayOrder() != null) {
            e.setDisplayOrder(request.displayOrder());
        }
        e = categoryRepository.save(e);
        return CatalogDtoMapper.toCategoryResponse(e);
    }

    @Transactional
    public void deleteCategory(Long id) {
        CatalogCategoryEntity cat = requireCategory(id);
        List<CatalogServiceEntity> services = catalogServiceRepository.findByCatalogCategory_Id(id);
        for (CatalogServiceEntity s : services) {
            s.setIsDeleted(Boolean.TRUE);
        }
        catalogServiceRepository.saveAll(services);
        cat.setIsDeleted(Boolean.TRUE);
        categoryRepository.save(cat);
    }

    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> listServicesInCategory(Long categoryId) {
        requireNonDeletedCategory(categoryId);
        return catalogServiceRepository
                .findByCatalogCategory_IdAndIsDeletedFalseOrderByDisplayOrderAscIdAsc(categoryId)
                .stream()
                .map(CatalogDtoMapper::toServiceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> listAllCatalogServices() {
        return catalogServiceRepository.findAllByIsDeletedFalse(Sort.by("id")).stream()
                .map(CatalogDtoMapper::toServiceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogServiceResponse getCatalogService(Long id) {
        return catalogServiceRepository
                .findWithCatalogCategoryByIdAndIsDeletedFalse(id)
                .map(CatalogDtoMapper::toServiceResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
    }

    @Transactional(readOnly = true)
    public CatalogServiceResponse getServiceInCategory(Long categoryId, Long serviceId) {
        requireNonDeletedCategory(categoryId);
        return catalogServiceRepository
                .findWithCatalogCategoryByIdAndCatalogCategory_IdAndIsDeletedFalse(serviceId, categoryId)
                .map(CatalogDtoMapper::toServiceResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
    }

    @Transactional
    public CatalogServiceResponse createService(Long categoryId, CatalogServiceCreateRequest request) {
        CatalogCategoryEntity category = requireNonDeletedCategory(categoryId);
        String name = InputSanitizer.normalizeName(request.name());
        assertServiceNameUniqueInCategory(category.getId(), name, null);
        CatalogServiceEntity e = new CatalogServiceEntity();
        e.setCatalogCategory(category);
        e.setName(name);
        e.setDescription(InputSanitizer.trimToNull(request.description()));
        e.setDisplayOrder(displayOrderVal(request.displayOrder()));
        applyServiceImages(e, request.imageUrls());
        e = catalogServiceRepository.save(e);
        return CatalogDtoMapper.toServiceResponse(e);
    }

    @Transactional
    public CatalogServiceResponse updateCatalogService(Long id, CatalogServiceUpdateRequest request) {
        if (request.name() == null
                && request.description() == null
                && request.imageUrls() == null
                && request.displayOrder() == null
                && request.categoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        CatalogServiceEntity e = requireCatalogService(id);
        if (request.categoryId() != null
                && !Objects.equals(request.categoryId(), e.getCatalogCategory().getId())) {
            CatalogCategoryEntity newCat = requireNonDeletedCategory(request.categoryId());
            e.setCatalogCategory(newCat);
        }
        Long targetCategoryId = e.getCatalogCategory().getId();
        String targetName = request.name() != null ? request.name().trim() : e.getName();
        assertServiceNameUniqueInCategory(targetCategoryId, targetName, id);
        if (request.name() != null) {
            e.setName(targetName);
        }
        if (request.description() != null) {
            e.setDescription(InputSanitizer.trimToNull(request.description()));
        }
        if (request.displayOrder() != null) {
            e.setDisplayOrder(request.displayOrder());
        }
        if (request.imageUrls() != null) {
            replaceServiceImages(e, request.imageUrls());
        }
        e = catalogServiceRepository.save(e);
        return CatalogDtoMapper.toServiceResponse(e);
    }

    @Transactional
    public void deleteCatalogService(Long id) {
        CatalogServiceEntity e = requireCatalogService(id);
        e.setIsDeleted(Boolean.TRUE);
        catalogServiceRepository.save(e);
    }

    private CatalogCategoryEntity requireCategory(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private CatalogCategoryEntity requireNonDeletedCategory(Long id) {
        return categoryRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private CatalogServiceEntity requireCatalogService(Long id) {
        return catalogServiceRepository
                .findWithCatalogCategoryById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
    }

    private void assertCategoryNameUnique(String name, Long excludeCategoryId) {
        boolean duplicate =
                excludeCategoryId == null
                        ? categoryRepository.existsByNameIgnoreCase(name)
                        : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, excludeCategoryId);
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_EXISTS_MESSAGE);
        }
    }

    private void assertServiceNameUniqueInCategory(Long categoryId, String name, Long excludeServiceId) {
        boolean duplicate =
                excludeServiceId == null
                        ? catalogServiceRepository.existsByCatalogCategory_IdAndNameIgnoreCase(categoryId, name)
                        : catalogServiceRepository.existsByCatalogCategory_IdAndNameIgnoreCaseAndIdNot(
                                categoryId, name, excludeServiceId);
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_EXISTS_MESSAGE);
        }
    }

    private void applyServiceImages(CatalogServiceEntity service, List<String> imageUrls) {
        List<String> normalized = ImageUrlValidator.normalizeImageUrlList(imageUrls, maxServiceImages);
        if (normalized == null || normalized.isEmpty()) {
            return;
        }
        replaceServiceImages(service, normalized);
    }

    private void replaceServiceImages(CatalogServiceEntity service, List<String> imageUrls) {
        List<String> normalized = ImageUrlValidator.normalizeImageUrlList(imageUrls, maxServiceImages);
        if (normalized == null) {
            return;
        }
        service.getImages().clear();
        int order = 0;
        for (String url : normalized) {
            CatalogServiceImageEntity image = new CatalogServiceImageEntity();
            image.setCatalogService(service);
            image.setImageUrl(url);
            image.setDisplayOrder(order++);
            service.getImages().add(image);
        }
    }

    private int displayOrderVal(Integer v) {
        return v == null ? 0 : v;
    }
}
