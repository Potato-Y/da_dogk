package com.github.dadogk.school.exception;

import com.github.dadogk.error.exception.BadRequestException;

public class SchoolMailDuplicatedException extends BadRequestException {

  public SchoolMailDuplicatedException(String mail) {
    super(mail);
  }
}
