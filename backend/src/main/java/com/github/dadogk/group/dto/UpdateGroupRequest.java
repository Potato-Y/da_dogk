package com.github.dadogk.group.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateGroupRequest {
    private String groupName;
    private String groupIntro;
    private String password;
}
