package com.github.dadogk.study.exception;

import com.github.dadogk.error.exception.NotFoundException;

public class NotFoundStudyException extends NotFoundException {
    public NotFoundStudyException(String message) {
        super(message);
    }
}
