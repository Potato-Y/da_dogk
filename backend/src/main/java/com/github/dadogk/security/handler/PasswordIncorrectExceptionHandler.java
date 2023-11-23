package com.github.dadogk.security.handler;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.handler.AbstractExceptionHandler;
import com.github.dadogk.security.exception.PasswordIncorrectException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class PasswordIncorrectExceptionHandler extends AbstractExceptionHandler<PasswordIncorrectException> {
    @Override
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleException(PasswordIncorrectException exception) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.toString());
    }
}