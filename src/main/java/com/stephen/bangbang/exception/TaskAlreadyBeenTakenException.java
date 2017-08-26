package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class TaskAlreadyBeenTakenException extends BaseRuntimeException {
    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail ed = new ErrorDetail("Task has been taken", this.getClass(), "任务已被占领");
        return new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_ACCEPTABLE;
    }
}
