package com.github.dadogk.group.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateGroupRequest {
    @NotNull
    private String groupName;
    private String password;
}
