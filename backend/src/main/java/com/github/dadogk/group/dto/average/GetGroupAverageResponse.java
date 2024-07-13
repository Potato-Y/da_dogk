package com.github.dadogk.group.dto.average;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetGroupAverageResponse {

  private Long groupId;
  private int year;
  private int month;
  private Long averageTime;
}
