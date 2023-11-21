package com.github.dadogk.group.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupType {
    SCHOOL("school"),
    COMMON("common");

    private String type;

    GroupType(String type) {
        this.type = type;
    }
}
