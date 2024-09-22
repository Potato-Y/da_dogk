package com.github.dadogk.group;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dadogk.enums.State;
import com.github.dadogk.group.dto.SignupGroupRequest;
import com.github.dadogk.group.dto.UpdateGroupRequest;
import com.github.dadogk.group.dto.create.CreateGroupRequest;
import com.github.dadogk.group.dto.create.GroupResponse;
import com.github.dadogk.group.entity.Group;
import com.github.dadogk.group.entity.GroupMemberRepository;
import com.github.dadogk.group.entity.GroupRepository;
import com.github.dadogk.group.entity.GroupType;
import com.github.dadogk.group.util.TestGroupUtil;
import com.github.dadogk.user.UserService;
import com.github.dadogk.user.dto.AddUserDto.AddUserRequest;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import com.github.dadogk.user.mapper.TestUserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class GroupApiControllerTest {

  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper; // JSON 직렬화, 역직렬화를 위한 클래스
  @Autowired
  private WebApplicationContext context;
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  UserRepository userRepository;
  @Autowired
  GroupRepository groupRepository;
  @Autowired
  GroupMemberRepository groupMemberRepository;
  @Autowired
  UserService userService;
  @Autowired
  GroupService groupService;

  private TestGroupUtil testGroupUtil;
  private TestUserUtil testUserUtil;

  @BeforeEach
  public void mockMvcSetup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    groupMemberRepository.deleteAll();
    groupRepository.deleteAll();
    userRepository.deleteAll();

    this.testGroupUtil = new TestGroupUtil(groupRepository, groupMemberRepository);
    this.testUserUtil = new TestUserUtil(bCryptPasswordEncoder, userRepository);
  }

  @DisplayName("createGroup(): 비공개 그룹 생성 성공")
  @WithMockUser(username = "host@mail.com")
  @Test
  public void successCreateGroupForPrivate() throws Exception {
    // given
    final String url = "/api/groups";
    final String email = "host@mail.com";
    final String password = "host";
    final String groupName = "test_group";
    final String groupIntro = "group info";
    final String groupPassword = "test_group";

    userService.save(new AddUserRequest(email, password, email));

    CreateGroupRequest request = new CreateGroupRequest(groupName, groupIntro, groupPassword);
    final String requestBody = objectMapper.writeValueAsString(request);

    // when
    ResultActions result = mockMvc.perform(
        post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    // then
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.groupName").value(groupName))
        .andExpect(jsonPath("$.groupIntro").value(groupIntro))
        .andExpect(jsonPath("$.state").value(State.ACTIVE.toString()))
        .andExpect(jsonPath("$.groupType").value(GroupType.COMMON.toString()))
        .andExpect(jsonPath("$.privacyState").value(true));
  }

  @DisplayName("createGroup(): 공개 그룹 생성 성공")
  @WithMockUser(username = "host@mail.com")
  @Test
  public void successCreateGroupForOpen() throws Exception {
    // given
    final String url = "/api/groups";
    final String email = "host@mail.com";
    final String password = "host";
    final String groupName = "test_group";
    final String groupIntro = "group info";

    userService.save(new AddUserRequest(email, password, email));

    CreateGroupRequest request = new CreateGroupRequest(groupName, groupIntro, null);
    final String requestBody = objectMapper.writeValueAsString(request);

    // when
    ResultActions result = mockMvc.perform(
        post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    // then
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.groupName").value(groupName))
        .andExpect(jsonPath("$.groupIntro").value(groupIntro))
        .andExpect(jsonPath("$.state").value(State.ACTIVE.toString()))
        .andExpect(jsonPath("$.groupType").value(GroupType.COMMON.toString()))
        .andExpect(jsonPath("$.privacyState").value(false));
  }

  @DisplayName("getGroup(): 특정 그룹 정보 조회 성공")
  @WithMockUser(username = "host@mail.com")
  @Test
  public void successGetGroup() throws Exception {
    // given
    final String url = "/api/groups/{groupId}";
    final String email = "host@mail.com";
    final String password = "host";

    final String groupName = "test_group";
    final String groupInfo = "group info";

    userService.save(new AddUserRequest(email, password, email));
    GroupResponse group = groupService.createGroup(
        new CreateGroupRequest(groupName, groupInfo, null));

    // when
    ResultActions result = mockMvc.perform(
        get(url.replace("{groupId}", String.valueOf(group.getId()))));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(group.getId()))
        .andExpect(jsonPath("$.groupName").value(groupName))
        .andExpect(jsonPath("$.groupIntro").value(groupInfo))
        .andExpect(jsonPath("$.hostUser.email").value(email))
        .andExpect(jsonPath("$.state").value(State.ACTIVE.toString()))
        .andExpect(jsonPath("$.groupType").value(GroupType.COMMON.toString()))
        .andExpect(jsonPath("$.privacyState").value(false))
        .andExpect(jsonPath("$.memberNumber").value(1));
  }

  @DisplayName("updateGroup(): 그룹 정보 수정 성공")
  @WithMockUser(username = "host@mail.com")
  @Test
  public void successUpdateGroup() throws Exception {
    // given
    final String url = "/api/groups/{groupId}";
    final String email = "host@mail.com";
    final String password = "host";

    final String groupName = "test_group";
    final String groupInfo = "group info";

    final String fixGroupName = "fix_group";
    final String fixGroupInfo = "fix_info";

    userService.save(new AddUserRequest(email, password, email));
    GroupResponse group = groupService.createGroup(
        new CreateGroupRequest(groupName, groupInfo, groupName));

    UpdateGroupRequest request = new UpdateGroupRequest();
    request.setGroupName(fixGroupName);
    request.setGroupIntro(fixGroupInfo);
    request.setPassword(fixGroupName);

    String requestBody = objectMapper.writeValueAsString(request);

    // when
    ResultActions result = mockMvc.perform(
        patch(url.replace("{groupId}", String.valueOf(group.getId())))
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(group.getId()))
        .andExpect(jsonPath("$.groupName").value(fixGroupName))
        .andExpect(jsonPath("$.groupIntro").value(fixGroupInfo))
        .andExpect(jsonPath("$.hostUser.email").value(email))
        .andExpect(jsonPath("$.state").value(State.ACTIVE.toString()))
        .andExpect(jsonPath("$.groupType").value(GroupType.COMMON.toString()))
        .andExpect(jsonPath("$.privacyState").value(true))
        .andExpect(jsonPath("$.memberNumber").value(1));
  }

  @DisplayName("deleteGroup(): 그룹 삭제 성공")
  @WithMockUser(username = "host@mail.com")
  @Test
  public void successDeleteGroup() throws Exception {
    // given
    final String url = "/api/groups/{groupId}";
    final String email = "host@mail.com";
    final String password = "host";

    final String groupName = "test_group";
    final String groupInfo = "group info";

    userService.save(new AddUserRequest(email, password, email));
    GroupResponse group = groupService.createGroup(
        new CreateGroupRequest(groupName, groupInfo, groupName));

    // when
    ResultActions result = mockMvc.perform(
        delete(url.replace("{groupId}", String.valueOf(group.getId()))));

    result.andExpect(status().isOk());
  }

  // TODO: 그룹원이 있을 때 삭제 여부 테스트

  @DisplayName("leaveGroup(): 그룹 나가기 성공")
  @WithMockUser(username = "user@mail.com")
  @Test
  public void successLeaveGroup() throws Exception {
    // given
    final String url = "/api/groups/{groupId}/members";

    User hostUser = userService.save(new AddUserRequest("host@mail.com", "host", "host"));
    User testUser = userService.save(new AddUserRequest("user@mail.com", "user", "user"));

    Group group = testGroupUtil.createCommonTestGroup(hostUser);
    testGroupUtil.signInGroup(testUser, group);

    // when
    ResultActions result = mockMvc.perform(
        delete(url.replace("{groupId}", String.valueOf(group.getId()))));

    // then
    result.andExpect(status().isOk());
  }

  @DisplayName("signupGroup(): 그룹 가입 성공")
  @WithMockUser(username = "user@mail.com")
  @Test
  public void successSignupGroup() throws Exception {
    // given
    final String url = "/api/groups/{groupId}/members";

    User hostUser = userService.save(new AddUserRequest("host@mail.com", "host", "host"));
    userService.save(new AddUserRequest("user@mail.com", "user", "user"));

    Group group = testGroupUtil.createCommonTestGroup(hostUser);

    SignupGroupRequest request = new SignupGroupRequest(null);
    String requestBody = objectMapper.writeValueAsString(request);

    // when
    ResultActions result = mockMvc.perform(
        post(url.replace("{groupId}", String.valueOf(group.getId()))).contentType(
            MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    result.andExpect(status().isOk());
  }


  @DisplayName("getGroupList(): 가입한 그룹 목록 조회 성공")
  @WithMockUser(username = "user@mail.com")
  @Test
  public void successGetGroupList() throws Exception {
    // given
    final String url = "/api/groups";

    User hostUser = testUserUtil.createTestUser("host@mail.com", "host");
    User testUser = testUserUtil.createTestUser("user@mail.com", "user");

    Group group1 = testGroupUtil.createCommonTestGroup(hostUser);
    Group group2 = testGroupUtil.createCommonTestGroup(hostUser);
    Group group3 = testGroupUtil.createCommonTestGroup(hostUser);
    testGroupUtil.signInGroup(testUser, group1);
    testGroupUtil.signInGroup(testUser, group2);
    testGroupUtil.signInGroup(testUser, group3);

    // when
    ResultActions result = mockMvc.perform(get(url));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].groupName").isNotEmpty())
        .andExpect(jsonPath("$[1].id").isNotEmpty())
        .andExpect(jsonPath("$[1].groupName").isNotEmpty())
        .andExpect(jsonPath("$[2].id").isNotEmpty())
        .andExpect(jsonPath("$[2].groupName").isNotEmpty());
  }

  @DisplayName("getGroupMembers(): 그룹원 목록 조회 성공")
  @WithMockUser(username = "user1@mail.com")
  @Test
  public void successGetGroupMembers() throws Exception {
    // given
    final String url = "/api/groups/{groupId}/members";

    User hostUser = testUserUtil.createTestUser("host@mail.com", "host");
    User testUser1 = testUserUtil.createTestUser("user1@mail.com", "user");
    User testUser2 = testUserUtil.createTestUser("user2@mail.com", "user");
    User testUser3 = testUserUtil.createTestUser("user3@mail.com", "user");

    Group group = testGroupUtil.createCommonTestGroup(hostUser);
    testGroupUtil.signInGroup(testUser1, group);
    testGroupUtil.signInGroup(testUser2, group);
    testGroupUtil.signInGroup(testUser3, group);

    // when
    ResultActions result = mockMvc.perform(
        get(url.replace("{groupId}", String.valueOf(group.getId()))));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").isNotEmpty())
        .andExpect(jsonPath("$[1].userId").isNotEmpty())
        .andExpect(jsonPath("$[2].userId").isNotEmpty())
        .andExpect(jsonPath("$[3].userId").isNotEmpty());
  }
}