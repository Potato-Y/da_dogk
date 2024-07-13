package com.github.dadogk.group.dto.average;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetGroupAverageRequest {

  private int year;
  private int month;

  public GetGroupAverageRequest() {
    LocalDate date = LocalDate.now();
    year = date.getYear();
    month = date.getMonth().getValue();
  }
}
