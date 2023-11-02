package com.github.dadogk.token.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateRequest {
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
}
