package com.stephen.bangbang.exception.exceptionhandler;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import com.stephen.bangbang.exception.task.TaskNotFoundException;
import com.stephen.bangbang.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<BaseResponse> duplicatedUser() {
        ErrorDetail errorDetail = new ErrorDetail("Duplicated user", DuplicatedUserException.class, "用户名已被使用");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
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
        BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    public ResponseEntity<BaseResponse> passwordIncorrect() {
        ErrorDetail errorDetail = new ErrorDetail("Password is incorrect", PasswordIncorrectException.class, "用户密码错误");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
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
}
