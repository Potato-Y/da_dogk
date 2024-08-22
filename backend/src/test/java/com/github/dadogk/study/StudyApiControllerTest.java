package com.github.dadogk.study;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dadogk.study.dto.api.create.CreateSubjectRequest;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudyRecordRepository;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.study.entity.StudySubjectRepository;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import com.github.dadogk.user.util.TestUserUtil;
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
class StudyApiControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper; // JSON 직렬화, 역직렬화를 위한 클래스
  @Autowired
  private WebApplicationContext context;
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  UserRepository userRepository;
  @Autowired
  StudySubjectRepository subjectRepository;
  @Autowired
  StudyRecordRepository recordRepository;

  TestUserUtil testUserUtil;

  @BeforeEach
  public void mockMvcSetup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    recordRepository.deleteAll();
    subjectRepository.deleteAll();
    userRepository.deleteAll();
    this.testUserUtil = new TestUserUtil(bCryptPasswordEncoder, userRepository);
  }

  @DisplayName("getSubjectList(): 사용자의 과목을 불러오는데 성공한다.")
  @WithMockUser("user@mail.com")
  @Test
  public void successGetUserSubjects() throws Exception {
    // given
    final String url = "/api/study/subjects/{userId}";
    final String email = "user@mail.com";
    final String password = "user";
    final String title = "test study";
    final String title2 = "test study2";

    User user = testUserUtil.createTestUser(email, password);
    subjectRepository.save(StudySubject.builder()
        .user(user)
        .title(title)
        .build());
    subjectRepository.save(StudySubject.builder()
        .user(user)
        .title(title2)
        .build());

    // when
    ResultActions result = mockMvc.perform(get(url.replace("{userId}", user.getId().toString())));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[0].user.userId").value(user.getId()))
        .andExpect(jsonPath("$[0].title").value(title))
        .andExpect(jsonPath("$[1].id").isNotEmpty())
        .andExpect(jsonPath("$[1].user.userId").value(user.getId()))
        .andExpect(jsonPath("$[1].title").value(title2));
  }

  @DisplayName("createSubject(): 사용자가 과목 생성에 성공한다.")
  @WithMockUser("user@mail.com")
  @Test
  public void successCreateSubjects() throws Exception {
    // given
    final String url = "/api/study/subjects";
    final String email = "user@mail.com";
    final String password = "user";
    final String title = "test subject";

    User user = testUserUtil.createTestUser(email, password);

    CreateSubjectRequest request = new CreateSubjectRequest(title);
    final String requestBody = objectMapper.writeValueAsString(request);

    // when
    ResultActions result = mockMvc.perform(
        post(url)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody));

    // then
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.user.userId").value(user.getId()))
        .andExpect(jsonPath("$.title").value(title));
  }

  @DisplayName("deleteSubject(): 사용자가 과목 제거에 성공한다.")
  @WithMockUser("user@mail.com")
  @Test
  public void successDeleteSubject() throws Exception {
    // given
    final String url = "/api/study/subjects/{subjectId}";
    final String email = "user@mail.com";
    final String password = "user";
    final String title = "test subject";

    User user = testUserUtil.createTestUser(email, password);
    StudySubject subject = subjectRepository.save(StudySubject.builder()
        .user(user)
        .title(title)
        .build());

    // when
    ResultActions result = mockMvc.perform(
        delete(url.replace("{subjectId}", subject.getId().toString())));

    // then
    result.andExpect(status().isOk());
  }

  @DisplayName("getCurrentUserRecodes(): 사용자 공부 기록을 조회하는데 성공한다.")
  @WithMockUser("user@mail.com")
  @Test
  public void successGetCurrentUserRecodes() throws Exception {
    // given
    final String url = "/api/study/recodes";
    final String email = "user@mail.com";
    final String password = "user";
    final String title = "test study";

    User user = testUserUtil.createTestUser(email, password);
    StudySubject subject = subjectRepository.save(
        StudySubject.builder().title("test study").user(user).build());
    StudyRecord record = recordRepository.save(
        StudyRecord.builder().user(user).subject(subject).build());
    recordRepository.save(record.updateEndAt());

    // when
    ResultActions result = mockMvc.perform(get(url));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].subject").isNotEmpty())
        .andExpect(jsonPath("$[0].subject.user.userId").value(user.getId()))
        .andExpect(jsonPath("$[0].subject.title").value(title))
        .andExpect(jsonPath("$[0].startAt").isNotEmpty())
        .andExpect(jsonPath("$[0].endAt").isNotEmpty());
  }

  @DisplayName("getUserRecodes(): 특정 사용자의 공부 기록을 조회하는데 성공한다.")
  @WithMockUser("user@mail.com")
  @Test
  public void successGetUserRecodes() throws Exception {
    // given
    final String url = "/api/study/recodes/{userId}";
    final String email = "user@mail.com";
    final String password = "user";
    final String title = "test study";

    testUserUtil.createTestUser(email, password);
    User user = testUserUtil.createTestUser(email + "_", password); // 다른 사용자 추가
    StudySubject subject = subjectRepository.save(
        StudySubject.builder().title("test study").user(user).build());
    StudyRecord record = recordRepository.save(
        StudyRecord.builder().user(user).subject(subject).build());
    recordRepository.save(record.updateEndAt());

    // when
    ResultActions result = mockMvc.perform(get(url.replace("{userId}", user.getId().toString()))
        .param("year", record.getStartAt().getYear() + "")
        .param("month", record.getStartAt().getMonth().getValue() + ""));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].subject").isNotEmpty())
        .andExpect(jsonPath("$[0].subject.user.userId").value(user.getId()))
        .andExpect(jsonPath("$[0].subject.title").value(title))
        .andExpect(jsonPath("$[0].startAt").isNotEmpty())
        .andExpect(jsonPath("$[0].endAt").isNotEmpty());
  }
}