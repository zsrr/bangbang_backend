package com.stephen.bangbang.dto;

public class UserResponse extends BaseResponse {
    String username;

    public UserResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
