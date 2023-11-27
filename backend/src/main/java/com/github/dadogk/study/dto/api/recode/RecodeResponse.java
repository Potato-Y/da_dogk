package com.github.dadogk.study.dto.api.recode;

import java.time.LocalDateTime;

import com.github.dadogk.study.dto.api.SubjectTitleResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecodeResponse {
    private SubjectTitleResponse subject;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
