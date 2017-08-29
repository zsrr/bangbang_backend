package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class JsonInvalidException extends BaseRuntimeException {
    public JsonInvalidException(Throwable cause) {
        super(cause);
    }

    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail ed = new ErrorDetail("Json string is invalid", JsonInvalidException.class, getCause().getMessage());
        return new BaseResponse(HttpStatus.BAD_REQUEST, ed);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
