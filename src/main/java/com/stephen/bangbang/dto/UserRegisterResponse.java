package com.stephen.bangbang.dto;

public class UserRegisterResponse extends BaseResponse {
    Long id;

    public UserRegisterResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
