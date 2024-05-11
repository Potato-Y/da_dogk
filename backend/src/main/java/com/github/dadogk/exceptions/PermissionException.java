package com.github.dadogk.exceptions;

import com.github.dadogk.error.exception.ForbiddenException;

public class PermissionException extends ForbiddenException {

  public PermissionException(String message) {
    super(message);
  }
}
