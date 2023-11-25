package com.github.dadogk.user.exception;

import com.github.dadogk.error.exception.NotFoundException;

public class NotFoundUserException extends NotFoundException {
    public NotFoundUserException(String message) {
        super(message);
    }
}
