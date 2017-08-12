package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class JPushException extends BaseRuntimeException {

    public JPushException(Throwable cause) {
        super(cause);
    }

    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail ed = new ErrorDetail("JPush exception happened", JPushException.class, getCause().getMessage());
        return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR, ed);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
