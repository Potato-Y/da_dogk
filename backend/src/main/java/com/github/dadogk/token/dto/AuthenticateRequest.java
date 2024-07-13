package com.github.dadogk.token.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateRequest {

  @NotBlank
  @Email
  private String email;
  @NotBlank
  private String password;
}
