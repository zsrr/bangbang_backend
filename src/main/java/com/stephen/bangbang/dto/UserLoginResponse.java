package com.stephen.bangbang.dto;

import com.stephen.bangbang.domain.User;

public class UserLoginResponse extends BaseResponse {
    Long id;
    String token;

    public UserLoginResponse(User user, String token) {
        this.id = user.getId();
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
