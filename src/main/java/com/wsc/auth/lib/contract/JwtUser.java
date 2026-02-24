package com.wsc.auth.lib.contract;

public interface JwtUser {

    Long getId();
    String getName();
    String getEmail();
    String getRole();
}
