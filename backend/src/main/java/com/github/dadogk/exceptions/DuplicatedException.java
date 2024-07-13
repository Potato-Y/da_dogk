package com.github.dadogk.exceptions;

import com.github.dadogk.error.exception.BadRequestException;

public class DuplicatedException extends BadRequestException {

  public DuplicatedException(String message) {
    super(message);
  }
}
