package com.is4tech.invoicemanagement.response;

import java.util.List;

public class LoginResponse {
        private String token;
        private long expiresIn;
        private List<String> authorities;
        private Integer userId; // Agregar userId

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

        public Integer getUserId() {
            return userId;
        }

        public LoginResponse setUserId(Integer userId) {
            this.userId = userId;
            return this;
        }
}
