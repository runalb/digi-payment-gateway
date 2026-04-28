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
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
        return categoryRepository.findByActiveTrue(Sort.by("displayOrder", "id")).stream()
                .map(CatalogService::toCatalogCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogCategoryResponse getCategory(Long id) {
        return toCatalogCategoryResponse(requireActiveCategory(id));
    }

    @Transactional
    public CatalogCategoryResponse createCategory(CatalogCategoryCreateRequest request) {
        String name = request.name().trim();
        assertCategoryNameUnique(name, null);
        CatalogCategoryEntity e = new CatalogCategoryEntity();
        e.setName(name);
        e.setDescription(blankDescriptionToNull(request.description()));
        e.setDisplayOrder(displayOrderVal(request.displayOrder()));
        e.setActive(request.active() == null ? Boolean.TRUE : request.active());
        e = categoryRepository.save(e);
        return toCatalogCategoryResponse(e);
    }

    @Transactional
    public CatalogCategoryResponse updateCategory(Long id, CatalogCategoryUpdateRequest request) {
        if (request.name() == null
                && request.description() == null
                && request.displayOrder() == null
                && request.active() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        CatalogCategoryEntity e = requireCategory(id);
        if (request.name() != null) {
            String name = request.name().trim();
            assertCategoryNameUnique(name, id);
            e.setName(name);
        }
        if (request.description() != null) {
            e.setDescription(blankDescriptionToNull(request.description()));
        }
        if (request.displayOrder() != null) {
            e.setDisplayOrder(request.displayOrder());
        }
        if (request.active() != null) {
            e.setActive(request.active());
        }
        e = categoryRepository.save(e);
        return toCatalogCategoryResponse(e);
    }

    @Transactional
    public void deleteCategory(Long id) {
        CatalogCategoryEntity cat = requireCategory(id);
        List<CatalogServiceEntity> services = catalogServiceRepository.findByCatalogCategory_Id(id);
        for (CatalogServiceEntity s : services) {
            s.setActive(false);
        }
        catalogServiceRepository.saveAll(services);
        cat.setActive(false);
        categoryRepository.save(cat);
    }

    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> listServicesInCategory(Long categoryId) {
        requireActiveCategory(categoryId);
        return catalogServiceRepository
                .findByCatalogCategory_IdAndActiveTrueOrderByDisplayOrderAscIdAsc(categoryId)
                .stream()
                .map(CatalogService::toCatalogServiceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> listAllCatalogServices() {
        return catalogServiceRepository.findAllByActiveTrue(Sort.by("id")).stream()
                .map(CatalogService::toCatalogServiceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogServiceResponse getCatalogService(Long id) {
        return catalogServiceRepository
                .findActiveByIdFetchCatalogCategory(id)
                .map(CatalogService::toCatalogServiceResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
    }

    @Transactional
    public CatalogServiceResponse createService(Long categoryId, CatalogServiceCreateRequest request) {
        CatalogCategoryEntity category = requireActiveCategory(categoryId);
        String name = request.name().trim();
        assertServiceNameUniqueInCategory(category.getId(), name, null);
        CatalogServiceEntity e = new CatalogServiceEntity();
        e.setCatalogCategory(category);
        e.setName(name);
        e.setDescription(blankDescriptionToNull(request.description()));
        e.setDisplayOrder(displayOrderVal(request.displayOrder()));
        e.setActive(request.active() == null ? Boolean.TRUE : request.active());
        e = catalogServiceRepository.save(e);
        return toCatalogServiceResponse(e);
    }

    @Transactional
    public CatalogServiceResponse updateCatalogService(Long id, CatalogServiceUpdateRequest request) {
        if (request.name() == null
                && request.description() == null
                && request.displayOrder() == null
                && request.active() == null
                && request.categoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must not be empty");
        }
        CatalogServiceEntity e = requireCatalogService(id);
        if (request.categoryId() != null
                && !Objects.equals(request.categoryId(), e.getCatalogCategory().getId())) {
            CatalogCategoryEntity newCat = requireActiveCategory(request.categoryId());
            e.setCatalogCategory(newCat);
        }
        Long targetCategoryId = e.getCatalogCategory().getId();
        String targetName = request.name() != null ? request.name().trim() : e.getName();
        assertServiceNameUniqueInCategory(targetCategoryId, targetName, id);
        if (request.name() != null) {
            e.setName(targetName);
        }
        if (request.description() != null) {
            e.setDescription(blankDescriptionToNull(request.description()));
        }
        if (request.displayOrder() != null) {
            e.setDisplayOrder(request.displayOrder());
        }
        if (request.active() != null) {
            e.setActive(request.active());
        }
        e = catalogServiceRepository.save(e);
        return toCatalogServiceResponse(e);
    }

    @Transactional
    public void deleteCatalogService(Long id) {
        CatalogServiceEntity e = requireCatalogService(id);
        e.setActive(false);
        catalogServiceRepository.save(e);
    }

    private CatalogCategoryEntity requireCategory(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private CatalogCategoryEntity requireActiveCategory(Long id) {
        return categoryRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private CatalogServiceEntity requireCatalogService(Long id) {
        return catalogServiceRepository
                .findByIdFetchCatalogCategory(id)
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
                e.getActive(),
                e.getCreatedDateTime(),
                e.getUpdatedDateTime());
    }

    private static CatalogServiceResponse toCatalogServiceResponse(CatalogServiceEntity e) {
        return new CatalogServiceResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getDisplayOrder(),
                e.getActive(),
                toCatalogCategoryResponse(e.getCatalogCategory()),
                e.getCreatedDateTime(),
                e.getUpdatedDateTime());
    }

    private String blankDescriptionToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        return s.trim();
    }

    private int displayOrderVal(Integer v) {
        return v == null ? 0 : v;
    }
}
