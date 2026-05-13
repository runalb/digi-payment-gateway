package com.runalb.ondemand_service.user.repository;

import com.runalb.ondemand_service.user.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    // By Id
    Optional<UserEntity> findById(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByIdWithRoles(Long id);



    // By Email
    Optional<UserEntity> findByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByEmailWithRoles(String email);




    // By Mobile Number
    Optional<UserEntity> findByMobileNumber(String mobileNumber);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByMobileNumberWithRoles(String mobileNumber);

    



    // Exists
    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByIdAndBusinesses_Id(Long id, Long businessId);

   

}
