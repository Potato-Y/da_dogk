package com.github.dadogk.group.exception;

import com.github.dadogk.error.exception.NotFoundException;

public class NotFoundGroupMemberException extends NotFoundException {
    public NotFoundGroupMemberException(String message) {
        super(message);
    }
}
