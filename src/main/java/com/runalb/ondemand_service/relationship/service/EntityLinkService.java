package com.runalb.ondemand_service.relationship.service;

import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.provider.repository.ProviderRepository;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityLinkService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;

    public EntityLinkService(UserRepository userRepository, ProviderRepository providerRepository) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
    }

    @Transactional
    public void linkUserToBusiness(UserEntity user, BusinessEntity business) {
        user.getBusinesses().add(business);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean userHasBusinessAccess(Long userId, Long businessId) {
        return userRepository.existsByIdAndBusinesses_Id(userId, businessId);
    }

    @Transactional(readOnly = true)
    public boolean userHasProviderAccess(Long userId, Long providerId) {
        return providerRepository.existsByIdAndUser_Id(providerId, userId);
    }
}
