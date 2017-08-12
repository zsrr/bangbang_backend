package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import org.springframework.http.HttpStatus;

public abstract class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException() {
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public BaseRuntimeException(String message) {
        super(message);
    }

    public abstract BaseResponse getBaseResponse();

    public abstract HttpStatus getHttpStatus();
}
