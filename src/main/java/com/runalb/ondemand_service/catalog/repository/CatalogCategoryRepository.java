package com.runalb.ondemand_service.catalog.repository;

import com.runalb.ondemand_service.catalog.entity.CatalogCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogCategoryRepository extends JpaRepository<CatalogCategoryEntity, Long> {}
