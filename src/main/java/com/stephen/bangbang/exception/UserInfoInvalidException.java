package com.stephen.bangbang.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "用户信息错误")
public class UserInfoInvalidException extends RuntimeException {
}
