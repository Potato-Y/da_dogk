package com.github.dadogk.token.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

  @NotBlank
  private String accessToken;
  @NotBlank
  private String refreshToken;
}
