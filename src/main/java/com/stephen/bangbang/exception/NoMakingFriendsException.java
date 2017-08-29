package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class NoMakingFriendsException extends BaseRuntimeException {
    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail ed = new ErrorDetail("Friend making request is not found", this.getClass(), "未能找到相应的好友请求");
        return new BaseResponse(HttpStatus.NOT_FOUND, ed);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
