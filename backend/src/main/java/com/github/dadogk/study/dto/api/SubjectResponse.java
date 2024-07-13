package com.github.dadogk.study.dto.api;

import com.github.dadogk.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubjectResponse {

  private Long id;
  private UserResponse user;
  private String title;
  private Long todayStudyTime;
}
