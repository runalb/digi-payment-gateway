package com.digirestro.digi_payment_gateway.entity;

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
@Table(name = "merchant")
public class MerchantEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String apiKey;

    @OneToOne(mappedBy = "merchant", fetch = FetchType.LAZY)
    private MerchantConfigEntity merchantConfig;

    @Column(nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @ManyToMany(mappedBy = "merchants")
    private List<UserEntity> users = new ArrayList<>();
}
