package com.github.dadogk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AddUserDto {

  @NoArgsConstructor // 기본 생성자 추가
  @AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
  @Getter
  @Setter
  public static class AddUserRequest {

    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  public static class AddUserResponse {

    private Long userId;
    private String email;
    private String nickname;
  }
}
