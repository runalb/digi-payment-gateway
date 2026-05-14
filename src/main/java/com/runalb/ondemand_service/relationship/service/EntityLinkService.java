package com.runalb.ondemand_service.relationship.service;

import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.business.repository.BusinessRepository;
import com.runalb.ondemand_service.provider.repository.ProviderRepository;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntityLinkService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final BusinessRepository businessRepository;

    public EntityLinkService(UserRepository userRepository, ProviderRepository providerRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional
    public void linkUserToBusiness(UserEntity user, BusinessEntity business) {
        user.getBusinesses().add(business);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean userHasBusinessAccess(Long userId, Long businessId) {
        return userRepository.existsByIdAndBusinessesId(userId, businessId);
    }

    @Transactional(readOnly = true)
    public boolean userHasProviderAccess(Long userId, Long providerId) {
        return providerRepository.existsByIdAndUserId(providerId, userId);
    }
}
