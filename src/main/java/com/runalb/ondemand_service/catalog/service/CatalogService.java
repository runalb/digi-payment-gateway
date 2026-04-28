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

    private final CatalogCategoryRepository categoryRepository;
    private final CatalogServiceRepository catalogServiceRepository;

    public CatalogService(
            CatalogCategoryRepository categoryRepository, CatalogServiceRepository catalogServiceRepository) {
        this.categoryRepository = categoryRepository;
        this.catalogServiceRepository = catalogServiceRepository;
    }

    @Transactional(readOnly = true)
    public List<CatalogCategoryResponse> listCategories() {
        return categoryRepository.findAll(Sort.by("displayOrder", "id")).stream()
                .map(CatalogService::toCatalogCategoryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogCategoryResponse getCategory(Long id) {
        return toCatalogCategoryResponse(requireCategory(id));
    }

    @Transactional
    public CatalogCategoryResponse createCategory(CatalogCategoryCreateRequest request) {
        CatalogCategoryEntity e = new CatalogCategoryEntity();
        e.setName(request.name().trim());
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
            e.setName(request.name().trim());
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
        requireCategory(id);
        if (catalogServiceRepository.countByCatalogCategory_Id(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot delete category while it has services");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CatalogServiceResponse> listServicesInCategory(Long categoryId) {
        requireCategory(categoryId);
        return catalogServiceRepository.findByCatalogCategory_IdOrderByDisplayOrderAscIdAsc(categoryId).stream()
                .map(CatalogService::toCatalogServiceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogServiceResponse getCatalogService(Long id) {
        return toCatalogServiceResponse(requireCatalogService(id));
    }

    @Transactional
    public CatalogServiceResponse createService(Long categoryId, CatalogServiceCreateRequest request) {
        CatalogCategoryEntity category = requireCategory(categoryId);
        CatalogServiceEntity e = new CatalogServiceEntity();
        e.setCatalogCategory(category);
        e.setName(request.name().trim());
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
            CatalogCategoryEntity newCat = requireCategory(request.categoryId());
            e.setCatalogCategory(newCat);
        }
        if (request.name() != null) {
            e.setName(request.name().trim());
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
        catalogServiceRepository.delete(e);
    }

    private CatalogCategoryEntity requireCategory(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    private CatalogServiceEntity requireCatalogService(Long id) {
        return catalogServiceRepository
                .findByIdFetchCatalogCategory(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
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
                e.getCatalogCategory().getId(),
                e.getCatalogCategory().getName(),
                e.getName(),
                e.getDescription(),
                e.getDisplayOrder(),
                e.getActive(),
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
