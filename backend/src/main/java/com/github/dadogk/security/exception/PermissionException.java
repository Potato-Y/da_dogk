package com.github.dadogk.security.exception;

import com.github.dadogk.error.exception.ForbiddenException;

public class PermissionException extends ForbiddenException {
    public PermissionException(String message) {
        super(message);
    }
}
