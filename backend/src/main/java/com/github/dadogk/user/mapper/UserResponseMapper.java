package com.github.dadogk.user.mapper;

import static com.github.dadogk.common.constants.DateTimeConstants.END_OF_DAY;
import static com.github.dadogk.study.util.StudyUtil.calculateStudyTime;

import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudyRecordRepository;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserResponseMapper {

  private final StudyRecordRepository studyRecordRepository;

  public UserResponse convertUserResponse(User findUser) {
    LocalDate nowDate = LocalDate.now();
    LocalDateTime startTime = nowDate.atStartOfDay();
    LocalDateTime endTime = nowDate.atTime(END_OF_DAY);

    List<StudyRecord> records = studyRecordRepository.findByUserAndStartAtBetween(findUser,
        startTime, endTime);
    long todayStudyTime = 0L;
    for (StudyRecord record : records) {
      todayStudyTime += calculateStudyTime(record);
    }

    return new UserResponse(findUser.getId(), findUser.getEmail(), findUser.getNickname(),
        todayStudyTime);
  }

}
