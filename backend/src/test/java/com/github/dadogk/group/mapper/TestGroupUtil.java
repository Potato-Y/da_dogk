package com.github.dadogk.group.mapper;

import com.github.dadogk.enums.State;
import com.github.dadogk.group.entity.Group;
import com.github.dadogk.group.entity.GroupMember;
import com.github.dadogk.group.entity.GroupMemberRepository;
import com.github.dadogk.group.entity.GroupRepository;
import com.github.dadogk.group.entity.GroupType;
import com.github.dadogk.user.entity.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TestGroupUtil {

  public static final String GROUP_TITLE = "test group";
  public static final String GROUP_INTRO = "group intro";

  private GroupRepository groupRepository;
  private GroupMemberRepository memberRepository;

  public Group createCommonTestGroup(User hostUser) {
    Group group = groupRepository.save(
        Group.builder()
            .groupName(GROUP_TITLE)
            .groupIntro(GROUP_INTRO)
            .hostUser(hostUser)
            .state(State.ACTIVE)
            .privacyState(false)
            .type(GroupType.COMMON)
            .build());

    memberRepository.save(
        GroupMember.builder()
            .group(group)
            .user(hostUser)
            .build());

    return group;
  }

  public void signInGroup(User user, Group group) {
    memberRepository.save(
        GroupMember.builder()
            .user(user)
            .group(group)
            .build());
  }
}
