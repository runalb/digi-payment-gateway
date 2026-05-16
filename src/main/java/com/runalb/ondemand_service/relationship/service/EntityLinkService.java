package com.runalb.ondemand_service.relationship.service;

import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.business.repository.BusinessRepository;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityLinkService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public EntityLinkService(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional
    public void linkUserToBusiness(UserEntity user, BusinessEntity business) {
        user.getBusinesses().add(business);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean userHasBusinessAccess(Long userId, Long businessId) {
        return businessRepository.existsByIdAndUsers_IdAndIsDeletedFalse(businessId, userId);
    }
}
