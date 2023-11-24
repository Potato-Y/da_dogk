package com.github.dadogk.group;

import com.github.dadogk.group.dto.SignupGroupRequest;
import com.github.dadogk.group.dto.create.CreateGroupRequest;
import com.github.dadogk.group.dto.create.GroupResponse;
import com.github.dadogk.group.entity.Group;
import com.github.dadogk.group.util.GroupUtil;
import com.github.dadogk.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/groups")
public class GroupApiController {
    private final GroupService groupService;
    private final SecurityUtil securityUtil;
    private final GroupUtil groupUtil;

    @PostMapping("")
    public ResponseEntity<GroupResponse> createGroup(@Validated @RequestBody CreateGroupRequest request) {
        GroupResponse groupResponse = groupService.createGroup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupResponse);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<String> leaveGroup(@PathVariable Long groupId) {
        groupService.leaveGroup(groupId, securityUtil.getCurrentUser());

        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<String> signupGroup(@PathVariable Long groupId,
                                              @Validated @RequestBody SignupGroupRequest request) {
        groupService.signupGroup(groupId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("")
    public ResponseEntity<List<GroupResponse>> getGroupList() {
        List<Group> inGroups = groupService.getGroupList(); // 들어있는 그룹 목록 요청

        List<GroupResponse> responses = new ArrayList<>(); // 응답할 수 있도록 가공
        for (Group group : inGroups) {
            responses.add(groupUtil.convertGroup(group));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(responses);
    }

    @GetMapping("/search") // 그룹 이름을 통해 검색
    public ResponseEntity<List<GroupResponse>> getSearchGroups(@RequestParam String groupName) {
        List<Group> groups = groupService.getSearchGroups(groupName);

        List<GroupResponse> groupResponses = new ArrayList<>(); // response로 가공
        for (Group group : groups) {
            groupResponses.add(groupUtil.convertGroup(group));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(groupResponses);
    }
}
