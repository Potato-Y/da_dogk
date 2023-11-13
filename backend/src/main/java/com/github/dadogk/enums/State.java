package com.github.dadogk.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum State {

    ACTIVE("active"), // 활성 상태
    DISABLED("disabled"), // 비활성 상태
    DELETE("delete"); // 삭제 상태

    private String state;

    State(String state) {
        this.state = state;
    }
}

