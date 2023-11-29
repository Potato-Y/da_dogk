package com.github.dadogk.user.util;

import com.github.dadogk.study.StudyService;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudyRecordRepository;
import com.github.dadogk.study.util.StudyUtil;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import com.github.dadogk.user.exception.NotFoundUserException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;
    private final StudyRecordRepository studyRecordRepository;

    /**
     * @param userId User db id
     * @return User
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException("Unexpected user"));
    }

    public UserResponse convertUserResponse(User findUser) {
        LocalDate nowDate = LocalDate.now();
        LocalDateTime startTime = nowDate.atStartOfDay();
        LocalDateTime endTime = nowDate.atTime(23, 59, 59);

        List<StudyRecord> records = studyRecordRepository.findByUserAndStartAtBetween(findUser, startTime, endTime);
        Long todayStudyTime = 0L;
        for (StudyRecord record : records) {
            LocalDateTime startAt = record.getStartAt();
            LocalDateTime endAt = record.getEndAt();
            if (endAt == null) { // 만약 아직 공부가 끝나지 않았다면 현재 시간으로
                endAt = LocalDateTime.now();
            }

            todayStudyTime += Duration.between(startAt, endAt).getSeconds();
        }

        return new UserResponse(findUser.getId(), findUser.getEmail(), findUser.getNickname(),todayStudyTime);
    }

}
