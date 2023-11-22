package com.github.dadogk.user.handler;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.handler.AbstractExceptionHandler;
import com.github.dadogk.user.exception.NotFoundUserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotFoundUserExceptionHandler extends AbstractExceptionHandler<NotFoundUserException> {

    @Override
    @ExceptionHandler(NotFoundUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(NotFoundUserException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.toString());
    }
}
