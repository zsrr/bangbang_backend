package com.stephen.bangbang.exception.exceptionhandler;

import com.stephen.bangbang.dto.BaseResponse;
import com.stephen.bangbang.dto.ErrorDetail;
import com.stephen.bangbang.exception.user.DuplicatedUserException;
import com.stephen.bangbang.exception.user.PasswordIncorrectException;
import com.stephen.bangbang.exception.user.UserInfoInvalidException;
import com.stephen.bangbang.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ControllerAdvice
@EnableWebMvc
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<BaseResponse> duplicatedUser() {
        ErrorDetail errorDetail = new ErrorDetail("Duplicated user", DuplicatedUserException.class, "用户名已被使用");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<BaseResponse> userNotFound() {
        ErrorDetail errorDetail = new ErrorDetail("User not found", UserNotFoundException.class, "用户不存在");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.NOT_FOUND, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserInfoInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<BaseResponse> userInfoInvalid() {
        ErrorDetail errorDetail = new ErrorDetail("User's information is invalid", UserInfoInvalidException.class, "用户信息不符合标准");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<BaseResponse> passwordIncorrect() {
        ErrorDetail errorDetail = new ErrorDetail("Password is incorrect", PasswordIncorrectException.class, "用户密码错误");
        BaseResponse baseResponse = new BaseResponse(HttpStatus.BAD_REQUEST, errorDetail);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
    }
}
