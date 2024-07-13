package com.github.dadogk.study.dto.socket;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class ConnectingGroupMembersResponse {

  private String result = "CONNECTING_GROUP_MEMBER";
  private List<GroupMembersResponse> groupMemberResponses = new ArrayList<>();

  public void addGroupMembers(GroupMembersResponse groupMembersResponse) {
    this.groupMemberResponses.add(groupMembersResponse);
  }
}
