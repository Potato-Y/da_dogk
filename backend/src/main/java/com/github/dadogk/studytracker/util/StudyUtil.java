package com.github.dadogk.studytracker.util;

import org.springframework.stereotype.Component;

import com.github.dadogk.studytracker.dto.api.SubjectTitleResponse;
import com.github.dadogk.studytracker.entity.StudySubject;
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
}
