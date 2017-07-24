package com.stephen.bangbang.dto;

import org.springframework.http.HttpStatus;

public class BaseResponse {
    int status;
    ErrorDetail error;

    public BaseResponse() {
        status = HttpStatus.OK.value();
        error = null;
    }

    public BaseResponse(HttpStatus status, ErrorDetail error) {
        this.status = status.value();
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public ErrorDetail getError() {
        return error;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(ErrorDetail error) {
        this.error = error;
    }
}
