package com.github.dadogk.group;

import com.github.dadogk.group.dto.GroupNameRequest;
import com.github.dadogk.group.dto.SignupGroupRequest;
import com.github.dadogk.group.dto.UpdateGroupRequest;
import com.github.dadogk.group.dto.average.GetGroupAverageRequest;
import com.github.dadogk.group.dto.average.GetGroupAverageResponse;
import com.github.dadogk.group.dto.create.CreateGroupRequest;
import com.github.dadogk.group.dto.create.GroupResponse;
import com.github.dadogk.group.entity.Group;
import com.github.dadogk.group.entity.GroupMember;
import com.github.dadogk.group.mapper.GroupResponseMapper;
import com.github.dadogk.security.CurrentUserProvider;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.mapper.UserResponseMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/groups")
public class GroupApiController {

  private final GroupService groupService;
  private final CurrentUserProvider currentUserProvider;
  private final GroupResponseMapper groupResponseMapper;
  private final UserResponseMapper userResponseMapper;

  @PostMapping("")
  public ResponseEntity<GroupResponse> createGroup(
      @Validated @RequestBody CreateGroupRequest request) {
    GroupResponse groupResponse = groupService.createGroup(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(groupResponse);
  }

  @GetMapping("/{groupId}")
  public ResponseEntity<GroupResponse> getGroup(@PathVariable Long groupId) {
    Group group = groupService.findGroup(groupId);

    return ResponseEntity.status(HttpStatus.OK).body(groupResponseMapper.convertGroup(group));
  }

  @PatchMapping("/{groupId}")
  public ResponseEntity<GroupResponse> updateGroup(@PathVariable Long groupId,
      @RequestBody UpdateGroupRequest request) {
    Group group = groupService.updateGroup(groupId, request);

    GroupResponse groupResponse = groupResponseMapper.convertGroup(group);
    return ResponseEntity.status(HttpStatus.OK).body(groupResponse);
  }

  @DeleteMapping("/{groupId}")
  public ResponseEntity<String> deleteGroup(@PathVariable Long groupId) {
    groupService.deleteGroup(groupId);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/{groupId}/members")
  public ResponseEntity<String> leaveGroup(@PathVariable Long groupId) {
    groupService.leaveGroup(groupId, currentUserProvider.getCurrentUser());

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/{groupId}/members")
  public ResponseEntity<String> signupGroup(@PathVariable Long groupId,
      @Validated @RequestBody SignupGroupRequest request) {
    groupService.signupGroup(groupId, request);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("")
  public ResponseEntity<List<GroupResponse>> getGroupList(GroupNameRequest request) {
    if (request.getGroupName() == null) {
      List<Group> inGroups = groupService.getGroupList(); // 들어있는 그룹 목록 요청
      List<GroupResponse> responses = inGroups.stream().map(groupResponseMapper::convertGroup)
          .toList();

      return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    List<Group> groups = groupService.getSearchGroups(request.getGroupName());
    List<GroupResponse> responses = groups.stream().map(groupResponseMapper::convertGroup).toList();

    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @GetMapping("/{groupId}/members")
  public ResponseEntity<List<UserResponse>> getGroupMembers(@PathVariable Long groupId) {
    List<GroupMember> groupMembers = groupService.getGroupMemberList(groupId); // 그룹원 가져오기

    List<UserResponse> userResponses = groupMembers.stream()
        .map(member -> userResponseMapper.convertUserResponse(member.getUser()))
        .toList();

    return ResponseEntity.status(HttpStatus.OK).body(userResponses);
  }

  @GetMapping("/{groupId}/study/average") // 특정 그룹의 평균 공부 측정 시간 (초)
  public ResponseEntity<GetGroupAverageResponse> getGroupAverage(@PathVariable Long groupId,
      GetGroupAverageRequest request) {
    Long result = groupService.getGroupAverage(groupId, request);
    GetGroupAverageResponse response = new GetGroupAverageResponse(groupId, request.getYear(),
        request.getMonth(), result);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
