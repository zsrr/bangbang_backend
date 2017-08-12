package com.stephen.bangbang.exception;


import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class TaskInfoInvalidException extends BaseRuntimeException {
    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail ed = new ErrorDetail("Task invalid", TaskInfoInvalidException.class, "任务信息不符合规范");
        return new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_ACCEPTABLE;
    }
}
