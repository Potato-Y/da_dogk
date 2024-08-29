package com.github.dadogk.error.handler;

import com.github.dadogk.error.dto.ErrorResponse;
import com.github.dadogk.error.exception.BadRequestException;
import com.github.dadogk.error.exception.ForbiddenException;
import com.github.dadogk.error.exception.NotFoundException;
import com.github.dadogk.error.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

  private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST) // status: 400
  public ErrorResponse handleException(BadRequestException e) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
  }

  @ExceptionHandler(UnauthorizedException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED) // status: 401
  public ErrorResponse handleException(UnauthorizedException e) {
    return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
  }

  @ExceptionHandler(ForbiddenException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN) // status: 403
  public ErrorResponse handleException(ForbiddenException e) {
    return new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND) // status: 404
  public ErrorResponse handleException(NotFoundException e) {
    return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // status: 500
  public ErrorResponse handleException(Exception e) {
    log.error(e.getMessage());
    return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR);
  }
}
