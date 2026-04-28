package com.runalb.ondemand_service.business.service;

import com.runalb.ondemand_service.business.dto.BusinessCreateRequest;
import com.runalb.ondemand_service.business.dto.BusinessResponse;
import com.runalb.ondemand_service.business.entity.BusinessEntity;
import com.runalb.ondemand_service.business.repository.BusinessRepository;
import com.runalb.ondemand_service.user.entity.UserEntity;
import com.runalb.ondemand_service.user.service.UserService;
import com.runalb.ondemand_service.util.StringNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final UserService userService;

    public BusinessService(
            BusinessRepository businessRepository,
            UserService userService) {
        this.businessRepository = businessRepository;
        this.userService = userService;
    }

   

    @Transactional
    public BusinessResponse createBusiness(BusinessCreateRequest request, UserEntity user) {
        BusinessEntity business = new BusinessEntity();
        business.setName(StringNormalizer.normalizeName(request.name()));
        business.setAddress(request.address());
        business.setMobileNumber(request.mobileNumber());
        business.setIsActive(true);
        business.setEmail(StringNormalizer.normalizeEmail(request.email()));
        business = businessRepository.save(business);

        userService.linkBusinessToUser(user.getId(), business);

        return new BusinessResponse(
            business.getId(),
            business.getName(),
            business.getEmail(),
            business.getAddress(),
            business.getMobileNumber(),
            business.getIsActive()
        );
    }

 


 
}
