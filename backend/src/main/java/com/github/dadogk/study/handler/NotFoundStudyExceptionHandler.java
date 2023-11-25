package com.github.dadogk.study.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.handler.AbstractExceptionHandler;
import com.github.dadogk.study.exception.NotFoundStudyException;

@RestControllerAdvice
public class NotFoundStudyExceptionHandler extends AbstractExceptionHandler<NotFoundStudyException> {
    @Override
    @ExceptionHandler(NotFoundStudyException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(NotFoundStudyException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.toString());
    }
}
