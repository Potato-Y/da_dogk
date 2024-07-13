package com.github.dadogk.study.dto.api.recode;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetUserRecodesRequest {

  private int year;
  private int month;

  public GetUserRecodesRequest() {
    LocalDate date = LocalDate.now();
    year = date.getYear();
    month = date.getMonth().getValue();
  }

  public GetUserRecodesRequest(int year, int month) {
    this.year = year;
    this.month = month;
  }
}
