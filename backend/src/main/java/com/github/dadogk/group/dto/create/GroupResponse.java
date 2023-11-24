package com.github.dadogk.group.dto.create;

import com.github.dadogk.enums.State;
import com.github.dadogk.group.entity.GroupType;
import com.github.dadogk.user.dto.UserResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupResponse {
    private Long id;
    private String groupName;
    private UserResponse hostUser;
    private State state;
    private GroupType groupType;
    private boolean privacyState;
    private LocalDateTime createAt;
    private int memberNumber;
}
