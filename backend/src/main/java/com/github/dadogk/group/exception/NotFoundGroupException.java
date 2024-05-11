package com.github.dadogk.group.exception;

import com.github.dadogk.error.exception.NotFoundException;

public class NotFoundGroupException extends NotFoundException {

  public NotFoundGroupException(String message) {
    super(message);
  }
}
