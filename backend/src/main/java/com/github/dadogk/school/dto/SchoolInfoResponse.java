package com.github.dadogk.school.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SchoolInfoResponse {

  private String schoolDomain;
  private String schoolName;
  private Long averageTime;
}
