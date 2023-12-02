package com.github.dadogk.group.dto.create;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateGroupRequest {
    @NotBlank
    private String groupName;
    private String groupIntro;
    private String password;
}
