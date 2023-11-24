package com.github.dadogk.group.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.handler.AbstractExceptionHandler;
import com.github.dadogk.group.exception.NotFoundGroupMemberException;

public class NotFoundGroupMemberExceptionHandler extends AbstractExceptionHandler<NotFoundGroupMemberException> {
    @Override
    @ExceptionHandler(NotFoundGroupMemberException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(NotFoundGroupMemberException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.toString());
    }
}
