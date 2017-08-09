package com.stephen.bangbang.exception.exceptionhandler;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import com.stephen.bangbang.exception.JsonInvalidException;
import com.stephen.bangbang.exception.task.TaskInfoInvalidException;
import com.stephen.bangbang.exception.task.TaskNotFoundException;
import com.stephen.bangbang.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<BaseResponse> duplicatedUser() {
        ErrorDetail errorDetail = new ErrorDetail("Duplicated user", DuplicatedUserException.class, "用户名已被使用");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BaseResponse> userNotFound() {
        ErrorDetail errorDetail = new ErrorDetail("User not found", UserNotFoundException.class, "用户不存在");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.NOT_FOUND, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserInfoInvalidException.class)
    public ResponseEntity<BaseResponse> userInfoInvalid() {
        ErrorDetail errorDetail = new ErrorDetail("User's information is invalid", UserInfoInvalidException.class, "用户信息不符合标准");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    public ResponseEntity<BaseResponse> passwordIncorrect() {
        ErrorDetail errorDetail = new ErrorDetail("Password is incorrect", PasswordIncorrectException.class, "用户密码错误");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.UNAUTHORIZED, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<BaseResponse> unAuthorized() {
        ErrorDetail errorDetail = new ErrorDetail("Authorization error", UnAuthorizedException.class, "用户身份验证失败");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.UNAUTHORIZED, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<BaseResponse> taskNotFound() {
        ErrorDetail errorDetail = new ErrorDetail("Task not found", TaskNotFoundException.class, "未找到此任务");
        BaseResponse br = new BaseResponse(HttpStatus.NOT_FOUND, errorDetail);
        return new ResponseEntity<BaseResponse>(br, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotCurrentUserException.class)
    public ResponseEntity<BaseResponse> notCurrentUser() {
        ErrorDetail ed = new ErrorDetail("Not current user", NotCurrentUserException.class, "非传递的Token对应的用户");
        BaseResponse br = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
        return new ResponseEntity<BaseResponse>(br, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(TaskInfoInvalidException.class)
    public ResponseEntity<BaseResponse> taskInfoInvalid() {
        ErrorDetail ed = new ErrorDetail("Task invalid", TaskInfoInvalidException.class, "任务信息不符合规范");
        BaseResponse br = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
        return new ResponseEntity<BaseResponse>(br, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> constraintViolation(ConstraintViolationException exception) {
        ErrorDetail ed = new ErrorDetail("Constraint not met", ConstraintViolationException.class, exception.getMessage());
        BaseResponse br = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
        return new ResponseEntity<BaseResponse>(br, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(JsonInvalidException.class)
    public ResponseEntity<BaseResponse> jsonInvalid() {
        ErrorDetail ed = new ErrorDetail("Json string is invalid", JsonInvalidException.class, "Json字符串不符合规范");
        BaseResponse br = new BaseResponse(HttpStatus.NOT_ACCEPTABLE, ed);
        return new ResponseEntity<BaseResponse>(br, HttpStatus.NOT_ACCEPTABLE);
    }
}
