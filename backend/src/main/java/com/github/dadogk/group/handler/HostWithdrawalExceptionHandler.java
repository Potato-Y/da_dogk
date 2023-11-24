package com.github.dadogk.group.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.handler.AbstractExceptionHandler;
import com.github.dadogk.group.exception.HostWithdrawalException;

@RestControllerAdvice
public class HostWithdrawalExceptionHandler extends AbstractExceptionHandler<HostWithdrawalException> {
    @Override
    @ExceptionHandler(HostWithdrawalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(HostWithdrawalException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.toString());
    }
}
