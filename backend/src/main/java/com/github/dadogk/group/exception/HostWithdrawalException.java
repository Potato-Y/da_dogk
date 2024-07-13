package com.github.dadogk.group.exception;

import com.github.dadogk.error.exception.BadRequestException;

public class HostWithdrawalException extends BadRequestException {

  public HostWithdrawalException(String message) {
    super(message);
  }
}
