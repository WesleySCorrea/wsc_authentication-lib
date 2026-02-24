package com.wsc.auth.lib.model;

import com.wsc.auth.lib.contract.JwtUser;

public class UserInfo implements JwtUser {
    private Long id;
    private String name;
    private String email;
    private String role;

    public UserInfo(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getRole() {
        return role;
    }
}
