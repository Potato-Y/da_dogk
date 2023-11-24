package com.github.dadogk.group;

import com.github.dadogk.group.dto.SignupGroupRequest;
import com.github.dadogk.group.dto.create.CreateGroupRequest;
import com.github.dadogk.group.dto.create.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping("")
    public ResponseEntity<GroupResponse> createGroup(@Validated @RequestBody CreateGroupRequest request) {
        GroupResponse groupResponse = groupService.createGroup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(groupResponse);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<String> signupGroup(@PathVariable Long groupId,
                                              @Validated @RequestBody SignupGroupRequest request) {
        groupService.signupGroup(groupId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
