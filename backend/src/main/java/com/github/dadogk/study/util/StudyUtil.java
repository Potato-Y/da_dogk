package com.github.dadogk.study.util;

import com.github.dadogk.study.dto.api.SubjectResponse;
import com.github.dadogk.study.dto.api.recode.RecodeResponse;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudyRecordRepository;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.util.UserUtil;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyUtil {

  private final UserUtil userUtil;
  private final StudyRecordRepository studyRecordRepository;

  public SubjectResponse convertSubjectResponse(StudySubject subject) {
    LocalDate today = LocalDate.now();
    LocalDateTime startDate = today.atStartOfDay();
    LocalDateTime endDate = today.atTime(23, 59, 59);

    Long totalTime = 0L;
    List<StudyRecord> records = studyRecordRepository.findBySubjectAndStartAtBetween(subject,
        startDate,
        endDate);
    for (StudyRecord record : records) {
      totalTime += calculateStudyTime(record);
    }

    UserResponse userResponse = userUtil.convertUserResponse(subject.getUser());
    SubjectResponse response = new SubjectResponse(subject.getId(), userResponse,
        subject.getTitle(), totalTime);

    return response;
  }

  public RecodeResponse convertRecodeResponse(StudyRecord record) {
    SubjectResponse subjectResponse = convertSubjectResponse(record.getSubject());

    return new RecodeResponse(subjectResponse, record.getStartAt(), record.getEndAt());
  }

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
