package com.github.dadogk.security.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.handler.AbstractExceptionHandler;
import com.github.dadogk.security.exception.PermissionException;

@RestControllerAdvice
public class PermissionExceptionHandler extends AbstractExceptionHandler<PermissionException> {
    @Override
    @ExceptionHandler(PermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleException(PermissionException exception) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), exception.toString());
    }
}
