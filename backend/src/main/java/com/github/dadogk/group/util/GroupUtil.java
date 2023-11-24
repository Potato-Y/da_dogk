package com.github.dadogk.group.util;

import com.github.dadogk.group.dto.create.GroupResponse;
import com.github.dadogk.group.entity.Group;
import com.github.dadogk.user.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GroupUtil {
    private final UserUtil userUtil;

    public GroupResponse convertGroup(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getGroupName(),
                userUtil.convertUserResponse(group.getHostUser()),
                group.getState(),
                group.getType(),
                group.isPrivacyState(),
                group.getCreatedAt(),
                group.getGroupMembers().size()
        );
    }
}
