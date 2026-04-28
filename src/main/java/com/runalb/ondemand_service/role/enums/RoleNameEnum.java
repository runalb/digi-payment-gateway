package com.runalb.ondemand_service.role.enums;

/** Stable identifiers stored in {@code roles.code} and JWT {@code roles} claims. */
public enum RoleNameEnum {
    CUSTOMER,
    PROVIDER,
    ADMIN,
    /** Elevated access; may use the same HTTP rules as {@link #ADMIN} plus broader ops where configured. */
    SUPER_ADMIN;
}
