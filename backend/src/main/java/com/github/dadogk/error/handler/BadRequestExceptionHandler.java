package com.github.dadogk.error.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.exception.BadRequestException;

@RestControllerAdvice
public class BadRequestExceptionHandler extends AbstractExceptionHandler<BadRequestException> {
    @Override
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(BadRequestException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.toString());
    }
}
