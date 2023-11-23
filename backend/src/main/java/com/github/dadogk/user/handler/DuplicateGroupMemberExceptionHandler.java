package com.github.dadogk.user.handler;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.handler.AbstractExceptionHandler;
import com.github.dadogk.user.exception.DuplicateGroupMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DuplicateGroupMemberExceptionHandler extends AbstractExceptionHandler<DuplicateGroupMemberException> {

    @Override
    @ExceptionHandler(DuplicateGroupMemberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(DuplicateGroupMemberException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.toString());
    }
}
