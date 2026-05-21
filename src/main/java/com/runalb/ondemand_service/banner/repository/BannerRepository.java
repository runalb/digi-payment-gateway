package com.runalb.ondemand_service.banner.repository;

import com.runalb.ondemand_service.banner.entity.BannerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {

    Optional<BannerEntity> findByIdAndIsDeletedFalse(Long id);

    List<BannerEntity> findByIsDeletedFalse(Sort sort);
}
