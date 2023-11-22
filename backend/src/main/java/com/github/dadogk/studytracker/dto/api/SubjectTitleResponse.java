package com.github.dadogk.studytracker.dto.api;

import com.github.dadogk.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubjectTitleResponse {
    private Long id;
    private UserResponse userResponse;
    private String title;
}
