package com.github.dadogk.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StudyStartRequest {
    private String type;
    private String accessToken;
    private Long subjectId;
}
