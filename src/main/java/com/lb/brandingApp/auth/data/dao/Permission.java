package com.lb.brandingApp.auth.data.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "permission")
@NoArgsConstructor
@Getter
@Setter
public class Permission {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 20)
    private String permissionName;

    @Column(length = 100)
    private String resourceUri;

    @Column(length = 10)
    private String httpMethod;

    @Column(length = 5)
    private boolean httpResource;

    @ManyToMany(mappedBy = "permissions")
    private Set<Team> teams;

}
