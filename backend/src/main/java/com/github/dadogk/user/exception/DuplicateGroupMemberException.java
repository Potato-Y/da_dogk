package com.github.dadogk.user.exception;

import com.github.dadogk.error.exception.BadRequestException;

public class DuplicateGroupMemberException extends BadRequestException {
    public DuplicateGroupMemberException(String message) {
        super(message);
    }
}
