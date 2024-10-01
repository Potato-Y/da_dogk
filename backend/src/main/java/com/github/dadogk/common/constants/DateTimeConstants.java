package com.github.dadogk.common.constants;

import java.time.LocalTime;

public class DateTimeConstants {

  public static final LocalTime END_OF_DAY = LocalTime.of(23, 59, 59);
  public static final int FIRST_DAY_OF_MONTH = 1;

  private DateTimeConstants() { // 인스턴스화 방지
  }
}
