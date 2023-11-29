package com.github.dadogk.study.dto.socket;

import com.github.dadogk.user.dto.UserResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupMembersResponse {
    private Long groupId;
    private List<UserResponse> user;
}
