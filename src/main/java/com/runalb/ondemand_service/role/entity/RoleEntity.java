package com.runalb.ondemand_service.role.entity;

import com.runalb.ondemand_service.common.persistence.AuditableEntity;
import com.runalb.ondemand_service.role.enums.RoleNameEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/** Lookup table of assignable roles ({@code CUSTOMER}, {@code PROVIDER}, {@code ADMIN}, {@code SUPER_ADMIN}). */
@Entity
@Table(name = "roles")
public class RoleEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 64)
    private RoleNameEnum roleName;

    @Column(length = 255)
    private String description;
}
