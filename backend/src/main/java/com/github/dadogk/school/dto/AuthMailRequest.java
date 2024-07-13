package com.github.dadogk.school.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthMailRequest {

  @NotBlank
  private String email;
}
