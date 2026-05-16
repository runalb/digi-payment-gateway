package com.runalb.ondemand_service.business.entity;

import com.runalb.ondemand_service.common.persistence.AuditableEntity;
import com.runalb.ondemand_service.user.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "business")
public class BusinessEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToOne(mappedBy = "business", fetch = FetchType.LAZY)
    private BusinessConfigEntity businessConfig;

    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    @ManyToMany(mappedBy = "businesses")
    private List<UserEntity> users = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = Boolean.FALSE;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @Column(columnDefinition = "TEXT")
    private String address; // use addrees table in future

    @Column(unique = true, length = 20)
    private String mobileNumber;

    @Column(nullable = false)
    private String businessType;
}
