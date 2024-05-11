package com.github.dadogk.security.exception;

import com.github.dadogk.error.exception.UnauthorizedException;

public class PasswordIncorrectException extends UnauthorizedException {

  public PasswordIncorrectException(String message) {
    super(message);
  }
}
