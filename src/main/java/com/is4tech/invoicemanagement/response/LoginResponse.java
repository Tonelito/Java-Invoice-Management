package com.is4tech.invoicemanagement.response;

import java.util.List;

public class LoginResponse {
    private String token;
    private long expiresIn;
    private List<String> authorities;

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public LoginResponse setAuthorities(List<String> authorities) {
        this.authorities = authorities;
        return this;
    }
}
