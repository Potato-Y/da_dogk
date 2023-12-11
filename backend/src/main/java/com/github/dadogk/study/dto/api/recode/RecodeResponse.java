package com.github.dadogk.study.dto.api.recode;

import java.time.LocalDateTime;

import com.github.dadogk.study.dto.api.SubjectResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecodeResponse {
    private SubjectResponse subject;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
