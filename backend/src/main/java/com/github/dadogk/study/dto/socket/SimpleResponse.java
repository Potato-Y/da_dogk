package com.github.dadogk.study.dto.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SimpleResponse {
    private String result;
    private String message;
}
