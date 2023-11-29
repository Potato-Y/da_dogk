package com.github.dadogk.utils;

import java.time.LocalDate;
import java.time.YearMonth;

public class DateTimeUtil {
    public static LocalDate getLastDayOfMonth(int year, int month) {
        // String monthFormat=Str
        LocalDate date = LocalDate.parse(String.format("%d-%02d-01", year, month));
        YearMonth yearMonth = YearMonth.from(date);

        return yearMonth.atEndOfMonth();
    }
}
