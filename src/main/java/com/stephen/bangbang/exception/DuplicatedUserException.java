package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class DuplicatedUserException extends BaseRuntimeException {

    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail errorDetail = new ErrorDetail("Duplicated user", DuplicatedUserException.class, "用户名已被使用");
        return new BaseResponse(HttpStatus.NOT_ACCEPTABLE, errorDetail);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_ACCEPTABLE;
    }
}
