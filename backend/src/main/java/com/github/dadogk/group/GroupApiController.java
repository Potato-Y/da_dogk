package com.github.dadogk.group;

import com.github.dadogk.group.dto.SignupGroupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/groups")
public class GroupApiController {
    private GroupService groupService;

    @PostMapping("/{groupId}/members")
    public ResponseEntity<String> signupGroup(@PathVariable Long groupId,
                                              @Validated @RequestBody SignupGroupRequest request) {
        groupService.signupGroup(groupId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
