package com.github.dadogk.group;

import static com.github.dadogk.study.util.StudyUtil.calculateStudyTime;

import com.github.dadogk.enums.State;
import com.github.dadogk.error.exception.NotFoundException;
import com.github.dadogk.exceptions.PermissionException;
import com.github.dadogk.group.dto.SignupGroupRequest;
import com.github.dadogk.group.dto.UpdateGroupRequest;
import com.github.dadogk.group.dto.average.GetGroupAverageRequest;
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
import com.github.dadogk.group.mapper.GroupResponseMapper;
import com.github.dadogk.security.CurrentUserProvider;
import com.github.dadogk.security.exception.PasswordIncorrectException;
import com.github.dadogk.security.util.PasswordUtil;
import com.github.dadogk.study.StudyService;
import com.github.dadogk.study.dto.api.recode.GetUserRecodesRequest;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.exception.DuplicateGroupMemberException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupService {

  private final StudyService studyService;
  private final GroupRepository groupRepository;
  private final GroupMemberRepository groupMemberRepository;
  private final GroupResponseMapper groupResponseMapper;
  private final CurrentUserProvider currentUserProvider;
  private final PasswordUtil passwordUtil;

  @Transactional
  public GroupResponse createGroup(CreateGroupRequest dto) {
    User hostUser = currentUserProvider.getCurrentUser(); // 그룹을 생성하려는 사람의 정보를 가져온다.

    // 그룹을 생성한다.
    Group group = Group.builder().groupName(dto.getGroupName()).groupIntro(dto.getGroupIntro())
        .hostUser(hostUser).state(State.ACTIVE).type(GroupType.COMMON).build();

    boolean isPassword = dto.getPassword() != null; // 암호 여부
    group.updatePrivacyState(isPassword);
    if (isPassword) {
      group.updatePassword(passwordUtil.convertPassword(dto.getPassword()));
    }
    groupRepository.save(group);

    GroupMember groupMember = GroupMember.builder().group(group).user(hostUser).build();
    groupMemberRepository.save(groupMember);

    return groupResponseMapper.convertGroup(group);
  }

  @Transactional
  public void signupGroup(Long groupId, SignupGroupRequest dto) {
    Optional<Group> group = groupRepository.findById(groupId);
    User user = currentUserProvider.getCurrentUser();

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
    if (member.isPresent()) { // 그룹에 이미 있는 경우 예외 발생
      log.warn("signupGroup: Duplicate group. userId={}, groupId={}", user.getId(), groupId);
      throw new DuplicateGroupMemberException("중복 가입");
    }

    GroupMember signupMember = GroupMember.builder().group(group.get()).user(user).build();

    groupMemberRepository.save(signupMember);
  }

  /**
   * 그룹을 탈퇴한다.
   *
   * @param groupId 탈퇴할 그룹 id
   * @param user    탈퇴할 유저
   */
  @Transactional
  public void leaveGroup(Long groupId, User user) {
    Optional<Group> group = groupRepository.findById(groupId);
    if (group.isEmpty()) {
      log.warn("leaveGroup: Not found group. userId={}, groupId={}", user.getId(), groupId);
      throw new NotFoundGroupException("그룹이 없음");
    }

    if (group.get().getHostUser().equals(user)) {
      log.warn("leaveGroup: Requested by host. userId={}, groupId={}", user.getId(),
          group.get().getId());
      throw new HostWithdrawalException("호스트는 탈퇴할 수 없음.");
    }

    Optional<GroupMember> groupMember = groupMemberRepository.findByGroupAndUser(group.get(), user);
    if (groupMember.isEmpty()) {
      log.warn("leaveGroup: Not found group member. userId={}, groupId={}", user.getId(), groupId);
      throw new NotFoundGroupMemberException("해당 유저가 그룹 멤버가 아님");
    }

    groupMemberRepository.delete(groupMember.get());
  }

  /**
   * 그룹을 삭제한다.
   *
   * @param groupId 삭제할 그룹 id
   */
  @Transactional
  public void deleteGroup(Long groupId) {
    User user = currentUserProvider.getCurrentUser();
    Optional<Group> group = groupRepository.findById(groupId);
    if (group.isEmpty()) {
      log.warn("deleteGroup: Not found group. userId={}, groupId={}", user.getId(), groupId);
      throw new NotFoundGroupException("그룹이 없음");
    }

    if (!group.get().getHostUser().equals(user)) { // host가 아니면 예외를 일으킨다.
      log.warn("deleteGroup: Not host user. userId={}, groupId={}", user.getId(), groupId);
      throw new PermissionException("호스트 유저가 아님");
    }

    groupRepository.delete(group.get()); // 그룹 삭제
  }

  /**
   * 사용자가 가입한 그룹의 목록을 가져온다.
   *
   * @return List<Group> 접속한 그룹 리스트
   */
  @Transactional(readOnly = true)
  public List<Group> getGroupList() {
    User user = currentUserProvider.getCurrentUser();
    List<GroupMember> inGroupMembers = groupMemberRepository.findAllByUser(user); // 가입한 그룹 조회

    return inGroupMembers.stream().map(GroupMember::getGroup).toList();
  }

  /**
   * 그룹 이름을 통해 검색
   *
   * @param groupName 검색할 그룹 이름
   * @return List<Group> 검색된 그룹
   */
  @Transactional(readOnly = true)
  public List<Group> getSearchGroups(String groupName) {
    return groupRepository.findByGroupNameContaining(groupName);
  }

  @Transactional(readOnly = true)
  public List<GroupMember> getGroupMemberList(Long groupId) {
    User user = currentUserProvider.getCurrentUser();
    Optional<Group> group = groupRepository.findById(groupId);
    if (group.isEmpty()) {
      log.warn("getGroupMemberList: Not found group. userId={}, groupId={}", user.getId(), groupId);
      throw new NotFoundGroupException("그룹이 없음");
    }

    // 유저가 해당 그룹원인지 확인
    Optional<GroupMember> findGroupMember = groupMemberRepository.findByGroupAndUser(group.get(),
        user);
    if (findGroupMember.isEmpty()) {
      log.warn("getGroupMemberList: Not found group member. userId={}, groupId={}", user.getId(),
          group.get().getId());
      throw new NotFoundGroupMemberException("그룹 멤버가 아님");
    }

    return groupMemberRepository.findAllByGroup(group.get());
  }

  /**
   * 초 단위로 그룹의 평균 시간 구하기
   *
   * @param groupId 해당 그룹 id
   * @param dto     검색 월
   * @return int 형 평균 공부 시간 (초)
   */
  @Transactional(readOnly = true)
  public Long getGroupAverage(Long groupId, GetGroupAverageRequest dto) {
    User user = currentUserProvider.getCurrentUser();
    Optional<Group> group = groupRepository.findById(groupId);
    if (group.isEmpty()) {
      log.warn("getGroupAverage: Not found group. userId={}, groupId={}", user.getId(), groupId);
      throw new NotFoundGroupException("그룹이 없음");
    }

    long totalStudyTime = 0L;
    int count = 0;

    List<GroupMember> members = groupMemberRepository.findAllByGroup(group.get());
    for (GroupMember member : members) { // 그룹원 별로 계산
      List<StudyRecord> records = studyService.getUserRecodes(member.getUser(),
          new GetUserRecodesRequest(dto.getYear(), dto.getMonth()));
      for (StudyRecord record : records) { // 공부 시간 초 단위로 계산
        totalStudyTime += calculateStudyTime(record);
      }
    }
    count += members.size(); // 사람 수 만큼 카운트 추가

    if (count == 0) { // 0인 경우 나눌 수 없다. 그대로 리턴한다.
      return totalStudyTime;
    }

    return totalStudyTime / count; // 평균으로 반환
  }

  @Transactional
  public Group updateGroup(Long groupId, UpdateGroupRequest dto) {
    User user = currentUserProvider.getCurrentUser();
    Optional<Group> group = groupRepository.findById(groupId);

    if (group.isEmpty()) {
      log.warn("updateGroupIntro: Not found group. userId={}, groupId={}", user.getId(), groupId);
      throw new NotFoundGroupException("그룹이 없음");
    }
    if (!group.get().getHostUser().equals(user)) { // host가 아니면 예외를 일으킨다.
      log.warn("deleteGroup: Not host user. userId={}, groupId={}", user.getId(), groupId);
      throw new PermissionException("호스트 유저가 아님");
    }

    Group updateGroup = group.get();

    if (dto.getGroupName() != null) { // 그룹 이름 업데이트
      updateGroup.updateGroupName(dto.getGroupName());
    }
    if (dto.getGroupIntro() != null) { // 그룹 설명 업데이트
      updateGroup.updateIntro(dto.getGroupIntro());
    }
    if (dto.getPassword() != null) { // 그룹 암호 업데이트
      if (dto.getPassword().isEmpty()) { // 비밀번호 삭제 후 공개
        updateGroup.updatePrivacyState(false);
        updateGroup.updatePassword(null);
      }
      if (!dto.getPassword().isBlank()) {
        updateGroup.updatePrivacyState(true);
        updateGroup.updatePassword(passwordUtil.convertPassword(dto.getPassword()));
      }
    }

    return groupRepository.save(updateGroup);
  }

  @Transactional(readOnly = true)
  public Group findGroup(Long groupId) {
    User user = currentUserProvider.getCurrentUser();
    Optional<Group> group = groupRepository.findById(groupId);

    if (group.isEmpty()) {
      log.warn("findGroup: Not found group. userId={}, groupId={}", user.getId(), groupId);
      throw new NotFoundException("그룹을  찾을 수 없음.");
    }

    return group.get();
  }
}
