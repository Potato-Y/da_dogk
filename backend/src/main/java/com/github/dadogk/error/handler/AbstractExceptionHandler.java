package com.github.dadogk.error.handler;

import com.github.dadogk.error.dto.ErrorResponse;

public abstract class AbstractExceptionHandler<T extends Exception> {

  public abstract ErrorResponse handleException(T exception);
}
