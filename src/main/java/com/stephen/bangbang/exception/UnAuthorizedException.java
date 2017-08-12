package com.stephen.bangbang.exception;


import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends BaseRuntimeException {
    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail errorDetail = new ErrorDetail("Authorization error", UnAuthorizedException.class, "用户身份验证失败");
        return new BaseResponse(HttpStatus.UNAUTHORIZED, errorDetail);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}
