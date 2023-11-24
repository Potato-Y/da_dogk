package com.github.dadogk.group;

import com.github.dadogk.enums.State;
import com.github.dadogk.group.dto.SignupGroupRequest;
import com.github.dadogk.group.dto.create.CreateGroupRequest;
import com.github.dadogk.group.dto.create.GroupResponse;
import com.github.dadogk.group.entity.Group;
import com.github.dadogk.group.entity.GroupMember;
import com.github.dadogk.group.entity.GroupMemberRepository;
import com.github.dadogk.group.entity.GroupRepository;
import com.github.dadogk.group.entity.GroupType;
import com.github.dadogk.group.exception.HostWithdrawalException;
import com.github.dadogk.group.exception.NotFoundGroupException;
import com.github.dadogk.group.exception.NotFoundGroupMemberException;
import com.github.dadogk.group.util.GroupUtil;
import com.github.dadogk.security.exception.PasswordIncorrectException;
import com.github.dadogk.security.util.PasswordUtil;
import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.exception.DuplicateGroupMemberException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupUtil groupUtil;
    private final SecurityUtil securityUtil;
    private final PasswordUtil passwordUtil;

    public GroupResponse createGroup(CreateGroupRequest dto) {
        User hostUser = securityUtil.getCurrentUser(); // 그룹을 생성하려는 사람의 정보를 가져온다.

        // 그룹을 생성한다.
        Group group = Group.builder()
                .groupName(dto.getGroupName())
                .hostUser(hostUser)
                .state(State.ACTIVE)
                .type(GroupType.COMMON)
                .build();

        boolean isPassword = dto.getPassword() != null; // 암호 여부
        group.updatePrivacyState(isPassword);
        if (isPassword) {
            group.updatePassword(passwordUtil.convertPassword(dto.getPassword()));
        }

        groupRepository.save(group);

        groupMemberRepository.save(GroupMember.builder()
                .group(group)
                .user(hostUser)
                .build());

        return groupUtil.convertGroup(group);
    }

    public void signupGroup(Long groupId, SignupGroupRequest dto) {
        Optional<Group> group = groupRepository.findById(groupId);
        User user = securityUtil.getCurrentUser();

        if (group.isEmpty()) {
            log.warn("signupGroup: Not found group. userId={}, groupId={}", user.getId(), groupId);
            throw new NotFoundGroupException("그룹이 없음");
        }

        if (group.get().isPrivacyState()) { // 암호가 있는 그룹일 경우
            if (!passwordUtil.matches(dto.getPassword(), group.get().getPassword())) {
                throw new PasswordIncorrectException("그룹 암호가 틀림");
            }
        }

        Optional<GroupMember> member = groupMemberRepository.findByGroupAndUser(group.get(), user);
        if (!member.isEmpty()) { // 그룹에 이미 있는 경우 예외 발생
            log.warn("signupGroup: Duplicate group. userId={}, groupId={}", user.getId(), groupId);
            throw new DuplicateGroupMemberException("중복 가입");
        }

        GroupMember signupMember = GroupMember.builder()
                .group(group.get())
                .user(user)
                .build();

        groupMemberRepository.save(signupMember);
    }

    /**
     * 그룹을 탈퇴한다.
     * 
     * @param groupId 탈퇴할 그룹 id
     * @param user    탈퇴할 유저
     */
    public void leaveGroup(Long groupId, User user) {
        Optional<Group> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            log.warn("leaveGroup: Not found group. userId={}, groupId={}", user.getId(), groupId);
            throw new NotFoundGroupException("그룹이 없음");
        }

        if (group.get().getHostUser().equals(user)) {
            log.warn("leaveGroup: Requested by host. userId={}, groupId={}", user.getId(), group.get().getId());
            throw new HostWithdrawalException("호스트는 탈퇴할 수 없음.");
        }

        Optional<GroupMember> groupMember = groupMemberRepository.findByGroupAndUser(group.get(), user);
        if (groupMember.isEmpty()) {
            log.warn("leaveGroup: Not found group member. userId={}, groupId={}", user.getId(), groupId);
            throw new NotFoundGroupMemberException("해당 유저가 그룹 멤버가 아님");
        }

        groupMemberRepository.delete(groupMember.get());
        log.info("leaveGroup: Leave group. userId={}, groupId={}", user.getId(), group.get().getId());
    }
}
