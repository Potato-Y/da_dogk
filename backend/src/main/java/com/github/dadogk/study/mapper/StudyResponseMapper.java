package com.github.dadogk.study.mapper;

import static com.github.dadogk.study.util.StudyUtil.calculateStudyTime;

import com.github.dadogk.study.dto.api.SubjectResponse;
import com.github.dadogk.study.dto.api.recode.RecodeResponse;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudyRecordRepository;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.mapper.UserResponseMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyResponseMapper {

  private final UserResponseMapper userResponseMapper;
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

    UserResponse userResponse = userResponseMapper.convertUserResponse(subject.getUser());
    SubjectResponse response = new SubjectResponse(subject.getId(), userResponse,
        subject.getTitle(), totalTime);

    return response;
  }

  public RecodeResponse convertRecodeResponse(StudyRecord record) {
    SubjectResponse subjectResponse = convertSubjectResponse(record.getSubject());

    return new RecodeResponse(subjectResponse, record.getStartAt(), record.getEndAt());
  }
}
