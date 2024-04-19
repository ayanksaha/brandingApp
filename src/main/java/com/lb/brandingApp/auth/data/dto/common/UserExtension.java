package com.lb.brandingApp.auth.data.dto.common;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;
import java.util.Set;

@Getter
public class UserExtension extends User {

    private final String name;
    private final String email;
    private final String phoneNumber;

    private final Set<PermissionDto> permissions;

    private final String homePage;

    private final boolean active;

    private final boolean defaultPass;

    public UserExtension(String username, String password, Collection<? extends GrantedAuthority> authorities,
             String name, String emailId, String phoneNo, Set<PermissionDto> permissions, String homePageUri,
                         boolean active, boolean defaultPass) {
        super(username, password, authorities);
        this.name = name;
        this.email = emailId;
        this.phoneNumber = phoneNo;
        this.permissions = permissions;
        this.homePage = homePageUri;
        this.active = active;
        this.defaultPass = defaultPass;
    }
}
