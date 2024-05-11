package com.github.dadogk.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddUserResponse {

  private Long userId;
  private String email;
  private String nickname;
}
