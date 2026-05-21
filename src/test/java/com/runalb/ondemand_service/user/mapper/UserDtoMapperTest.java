package com.runalb.ondemand_service.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.runalb.ondemand_service.role.entity.RoleEntity;
import com.runalb.ondemand_service.role.enums.RoleNameEnum;
import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.entity.UserEntity;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserDtoMapperTest {

    @Test
    void toResponse_mapsUserWithoutRoles() {
        UserEntity user = new UserEntity();
        user.setId(7L);
        user.setEmail("user@example.com");
        user.setMobileNumber("+911234567890");
        user.setName("Test User");
        user.setIsVerified(true);

        UserResponse response = UserDtoMapper.toResponse(user);

        assertEquals(7L, response.id());
        assertEquals("user@example.com", response.email());
        assertEquals("+911234567890", response.mobileNumber());
        assertEquals("Test User", response.name());
        assertEquals(true, response.isVerified());
        assertTrue(response.roles().isEmpty());
    }

    @Test
    void toResponse_mapsDistinctSortedRoles() {
        UserEntity user = new UserEntity();
        user.setId(8L);
        user.setEmail("admin@example.com");
        user.setName("Admin");
        user.setIsVerified(false);
        user.setRoles(Set.of(role(RoleNameEnum.PROVIDER), role(RoleNameEnum.CUSTOMER)));

        UserResponse response = UserDtoMapper.toResponse(user);

        assertEquals(List.of("CUSTOMER", "PROVIDER"), response.roles());
    }

    private static RoleEntity role(RoleNameEnum roleName) {
        RoleEntity role = new RoleEntity();
        role.setRoleName(roleName);
        return role;
    }
}
