package com.runalb.ondemand_service.catalog.service;

import com.runalb.ondemand_service.catalog.dto.CatalogCategoryCreateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogCategoryUpdateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceCreateRequest;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceResponse;
import com.runalb.ondemand_service.catalog.dto.CatalogServiceUpdateRequest;
import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import com.runalb.ondemand_service.catalog.entity.CatalogServiceEntity;
import com.runalb.ondemand_service.catalog.repository.CatalogCategoryRepository;
import com.runalb.ondemand_service.catalog.repository.CatalogServiceRepository;
import com.runalb.ondemand_service.util.InputSanitizer;
import java.util.List;
import java.util.Objects;
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

    public CatalogService(
            CatalogCategoryRepository categoryRepository, CatalogServiceRepository catalogServiceRepository) {
        this.categoryRepository = categoryRepository;
        this.catalogServiceRepository = catalogServiceRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogCategoryResponse> listCategories() {
        return categoryRepository.findByIsDeletedFalse(Sort.by("displayOrder", "id")).stream()
                .map(CatalogService::toCatalogCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogCategoryResponse getCategory(Long id) {
        return toCatalogCategoryResponse(requireNonDeletedCategory(id));
    }

    @Transactional
    public CatalogCategoryResponse createCategory(CatalogCategoryCreateRequest request) {
        String name = request.name().trim();
        assertCategoryNameUnique(name, null);
        CatalogCategoryEntity e = new CatalogCategoryEntity();
        e.setName(name);
        e.setDescription(InputSanitizer.trimToNull(request.description()));
        e.setDisplayOrder(displayOrderVal(request.displayOrder()));
        e = categoryRepository.save(e);
        return toCatalogCategoryResponse(e);
    }

    @Transactional
    public CatalogCategoryResponse updateCategory(Long id, CatalogCategoryUpdateRequest request) {
        if (request.name() == null
                && request.description() == null
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
        if (request.displayOrder() != null) {
            e.setDisplayOrder(request.displayOrder());
        }
        e = categoryRepository.save(e);
        return toCatalogCategoryResponse(e);
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
                .map(CatalogService::toCatalogServiceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> listAllCatalogServices() {
        return catalogServiceRepository.findAllByIsDeletedFalse(Sort.by("id")).stream()
                .map(CatalogService::toCatalogServiceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogServiceResponse getCatalogService(Long id) {
        return catalogServiceRepository
                .findWithCatalogCategoryByIdAndIsDeletedFalse(id)
                .map(CatalogService::toCatalogServiceResponse)
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
        e = catalogServiceRepository.save(e);
        return toCatalogServiceResponse(e);
    }

    @Transactional
    public CatalogServiceResponse updateCatalogService(Long id, CatalogServiceUpdateRequest request) {
        if (request.name() == null
                && request.description() == null
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
        e = catalogServiceRepository.save(e);
        return toCatalogServiceResponse(e);
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

    private static CatalogCategoryResponse toCatalogCategoryResponse(CatalogCategoryEntity e) {
        return new CatalogCategoryResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getDisplayOrder(),
                e.getCreatedDateTime(),
                e.getUpdatedDateTime());
    }

    private static CatalogServiceResponse toCatalogServiceResponse(CatalogServiceEntity e) {
        return new CatalogServiceResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getDisplayOrder(),
                toCatalogCategoryResponse(e.getCatalogCategory()),
                e.getCreatedDateTime(),
                e.getUpdatedDateTime());
    }

    private int displayOrderVal(Integer v) {
        return v == null ? 0 : v;
    }
}
