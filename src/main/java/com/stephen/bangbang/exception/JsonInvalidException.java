package com.stephen.bangbang.exception;

public class JsonInvalidException extends RuntimeException {
    public JsonInvalidException(Throwable cause) {
        super(cause);
    }

    public JsonInvalidException() {
    }
}
