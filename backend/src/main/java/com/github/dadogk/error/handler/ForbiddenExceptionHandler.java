package com.github.dadogk.error.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.exception.ForbiddenException;

@RestControllerAdvice
public class ForbiddenExceptionHandler extends AbstractExceptionHandler<ForbiddenException> {
    @Override
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleException(ForbiddenException exception) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), exception.toString());
    }
}
