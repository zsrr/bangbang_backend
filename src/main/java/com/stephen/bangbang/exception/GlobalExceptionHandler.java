package com.stephen.bangbang.exception;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import com.stephen.bangbang.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseRuntimeException.class)
    public ResponseEntity<BaseResponse> baseRuntimeException(BaseRuntimeException e) {
        return new ResponseEntity<BaseResponse>(e.getBaseResponse(), e.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> constraintViolation(ConstraintViolationException exception) {
        ErrorDetail ed = new ErrorDetail("Constraint not met", ConstraintViolationException.class, exception.getMessage());
        BaseResponse br = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
        return new ResponseEntity<BaseResponse>(br, HttpStatus.NOT_ACCEPTABLE);
    }
}