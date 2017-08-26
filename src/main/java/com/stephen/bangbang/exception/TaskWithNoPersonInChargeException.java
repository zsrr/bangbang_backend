package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import org.springframework.http.HttpStatus;

public class TaskWithNoPersonInChargeException extends BaseRuntimeException {
    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail ed = new ErrorDetail("This task has no person in charge", this.getClass(), "此任务尚无人掌管");
        return new BaseResponse(HttpStatus.BAD_REQUEST, ed);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
