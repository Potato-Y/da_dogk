package com.github.dadogk.study.util;

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
}
