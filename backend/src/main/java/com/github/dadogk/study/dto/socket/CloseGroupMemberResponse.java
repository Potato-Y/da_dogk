package com.github.dadogk.study.dto.socket;

import com.github.dadogk.user.dto.UserResponse;
import lombok.Getter;

@Getter
public class CloseGroupMemberResponse {

  private String result = "CLOSE_GROUP_MEMBER";
  private Long groupId;
  private UserResponse user;

  public CloseGroupMemberResponse(Long groupId, UserResponse userResponse) {
    this.groupId = groupId;
    this.user = userResponse;
  }
}
