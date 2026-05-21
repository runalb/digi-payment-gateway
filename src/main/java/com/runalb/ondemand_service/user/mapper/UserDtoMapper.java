package com.runalb.ondemand_service.user.mapper;

import com.runalb.ondemand_service.user.dto.UserResponse;
import com.runalb.ondemand_service.user.entity.UserEntity;
import java.util.List;

public final class UserDtoMapper {

    private UserDtoMapper() {}

    public static UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getName(),
                user.getIsVerified(),
                roleNames(user));
    }

    private static List<String> roleNames(UserEntity user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return List.of();
        }
        return user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .distinct()
                .sorted()
                .toList();
    }
}
