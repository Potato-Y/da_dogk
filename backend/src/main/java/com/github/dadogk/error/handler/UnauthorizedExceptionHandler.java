package com.github.dadogk.error.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.exception.UnauthorizedException;

@RestControllerAdvice
public class UnauthorizedExceptionHandler extends AbstractExceptionHandler<UnauthorizedException> {
    @Override
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleException(UnauthorizedException exception) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.toString());
    }
}
