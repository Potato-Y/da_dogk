package com.github.dadogk.group.mapper;

import com.github.dadogk.group.dto.create.GroupResponse;
import com.github.dadogk.group.entity.Group;
import com.github.dadogk.user.mapper.UserResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GroupResponseMapper {

  private final UserResponseMapper userResponseMapper;

  public GroupResponse convertGroup(Group group) {
    return new GroupResponse(group.getId(), group.getGroupName(), group.getGroupIntro(),
        userResponseMapper.convertUserResponse(group.getHostUser()), group.getState(),
        group.getType(),
        group.isPrivacyState(), group.getCreatedAt(), group.getGroupMembers().size());
  }
}
