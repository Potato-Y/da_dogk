package com.github.dadogk.studytracker.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SimpleResponse {
    private String result;
    private String message;
}
