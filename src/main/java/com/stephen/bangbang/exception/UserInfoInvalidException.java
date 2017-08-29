package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class UserInfoInvalidException extends BaseRuntimeException {
    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail errorDetail = new ErrorDetail("User's information is invalid", UserInfoInvalidException.class, "用户信息不符合标准");
        return new BaseResponse(HttpStatus.BAD_REQUEST, errorDetail);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
