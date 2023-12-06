package com.github.dadogk.study.util;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

import com.github.dadogk.study.dto.api.SubjectTitleResponse;
import com.github.dadogk.study.dto.api.recode.RecodeResponse;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.util.UserUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StudyUtil {
    private final UserUtil userUtil;

    public SubjectTitleResponse convertSubjectToTitleResponse(StudySubject subject) {
        UserResponse userResponse = userUtil.convertUserResponse(subject.getUser());
        SubjectTitleResponse response = new SubjectTitleResponse(subject.getId(), userResponse, subject.getTitle());

        return response;
    }

    public RecodeResponse convertRecodeResponse(StudyRecord record) {
        SubjectTitleResponse subjectResponse = convertSubjectToTitleResponse(record.getSubject());

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
