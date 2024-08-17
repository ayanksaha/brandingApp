package com.lb.brandingApp.auth.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "permission")
@NoArgsConstructor
@Getter
@Setter
public class Permission {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 25)
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
