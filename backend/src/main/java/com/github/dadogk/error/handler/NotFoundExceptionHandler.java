package com.github.dadogk.error.handler;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotFoundExceptionHandler extends AbstractExceptionHandler<NotFoundException> {

  @Override
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleException(NotFoundException exception) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.toString());
  }
}
