package com.stephen.bangbang.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "该用户已经存在")
public class DuplicatedUserException extends RuntimeException {
}
