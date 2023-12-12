package com.github.dadogk.school.exception;

import com.github.dadogk.error.exception.BadRequestException;

public class MailSendException extends BadRequestException {
    public MailSendException(String message) {
        super(message);
    }
}
