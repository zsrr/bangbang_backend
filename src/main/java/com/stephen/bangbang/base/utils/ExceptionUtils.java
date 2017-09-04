package com.stephen.bangbang.base.utils;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;

public class ExceptionUtils {
    private static final Map<Class<? extends Throwable>, Integer> SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP = new HashMap<>(16);

    static {
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(NoSuchRequestHandlingMethodException.class, SC_NOT_FOUND);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpRequestMethodNotSupportedException.class, SC_METHOD_NOT_ALLOWED);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMediaTypeNotSupportedException.class, SC_UNSUPPORTED_MEDIA_TYPE);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMediaTypeNotAcceptableException.class, SC_NOT_ACCEPTABLE);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MissingPathVariableException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MissingServletRequestParameterException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(ServletRequestBindingException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(ConversionNotSupportedException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(TypeMismatchException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMessageNotReadableException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMessageNotWritableException.class, SC_INTERNAL_SERVER_ERROR);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MethodArgumentNotValidException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MissingServletRequestPartException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(BindException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(NoHandlerFoundException.class, SC_NOT_FOUND);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(AsyncRequestTimeoutException.class, SC_SERVICE_UNAVAILABLE);
    }

    public static int getStatusCode(Class<? extends Throwable> clazz) {
        if (SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.containsKey(clazz)) {
            return SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.get(clazz);
        }
        return 500;
    }
}
