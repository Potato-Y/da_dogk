package com.github.dadogk.study.util;

import com.github.dadogk.study.entity.StudyRecord;
import java.time.Duration;
import java.time.LocalDateTime;

public class StudyUtil {

  /**
   * Record의 공부 시간을 계산한다. 만약 공부가 끝나지 않았다면 현재 시간을 기점으로 계산한다.
   *
   * @param record 공부 기록
   * @return 공부 시간(초)
   */
  public static Long calculateStudyTime(StudyRecord record) {
    LocalDateTime startAt = record.getStartAt();
    LocalDateTime endAt = record.getEndAt();
    if (endAt == null) { // 공부가 아직 끝나지 않았다면 현재 시간으로 설정
      endAt = LocalDateTime.now();
    }

    return Duration.between(startAt, endAt).getSeconds();
  }
}
