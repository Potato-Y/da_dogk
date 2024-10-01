package com.github.dadogk.school;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dadogk.school.dto.VerifyEmailRequest;
import com.github.dadogk.school.entity.MailAuthInfo;
import com.github.dadogk.school.entity.MailAuthInfoRepository;
import com.github.dadogk.school.entity.School;
import com.github.dadogk.school.entity.SchoolMember;
import com.github.dadogk.school.entity.SchoolMemberRepository;
import com.github.dadogk.school.entity.SchoolRepository;
import com.github.dadogk.user.UserService;
import com.github.dadogk.user.dto.AddUserDto.AddUserRequest;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import java.util.Optional;
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
class SchoolApiControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper; // JSON 직렬화, 역직렬화를 위한 클래스
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private UserService userService;
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  UserRepository userRepository;
  @Autowired
  SchoolRepository schoolRepository;
  @Autowired
  SchoolMemberRepository schoolMemberRepository;
  @Autowired
  private MailAuthInfoRepository mailAuthInfoRepository;

  private static final String TEST_SCHOOL_DOMAIN = "test.ac.kr";
  private static final String TEST_SCHOOL_NAME = "테스트대학교";

  @BeforeEach
  public void mockMvcSetup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    mailAuthInfoRepository.deleteAll();
    schoolMemberRepository.deleteAll();
    schoolRepository.deleteAll();
    userRepository.deleteAll();

    schoolRepository.save( // 테스트용 학교 데이터 추가
        School.builder().domain(TEST_SCHOOL_DOMAIN).name(TEST_SCHOOL_NAME).build());
  }

  /// Docker SMTP(image: mailhog/mailhog)가 구성된 환경에서 테스트 ///
  //  @DisplayName("requestAuthEmail(): 대학교 이메일 인증 요청 성공")
  //  @WithMockUser("user@mail.com")
  //  @Test
  //  public void successRequestAuthEmail() throws Exception {
  //    // given
  //    final String url = "/api/school/auth/mail";
  //    final String email = "user@mail.com";
  //    final String password = "user";
  //    final String schoolEmail = "test@" + TEST_SCHOOL_DOMAIN;
  //    User user = userService.save(new AddUserRequest(email, password, email));
  //
  //    AuthMailRequest request = new AuthMailRequest();
  //    request.setEmail(schoolEmail);
  //
  //    String requestBody = objectMapper.writeValueAsString(request);
  //
  //    // when
  //    ResultActions result = mockMvc.perform(
  //        post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));
  //
  //    // then
  //    result.andExpect(status().isOk());
  //
  //    Optional<MailAuthInfo> authInfo = mailAuthInfoRepository.findByUser(user);
  //    assertThat(authInfo).isPresent();
  //    assertThat(authInfo.get().getCode()).isNotEmpty();
  //    assertThat(authInfo.get().getMail()).isEqualTo(schoolEmail);
  //  }

  @DisplayName("verifyEmail(): 사용자의 대학교 인증 코드 검증에 성공")
  @WithMockUser("user@mail.com")
  @Test
  public void successVerifyEmail() throws Exception {
    // given
    final String url = "/api/school/auth/verify";
    final String email = "user@mail.com";
    final String password = "user";
    final String schoolEmail = "test@" + TEST_SCHOOL_DOMAIN;
    final String code = "code";
    User user = userService.save(new AddUserRequest(email, password, email));

    School school = schoolRepository.findByDomain(TEST_SCHOOL_DOMAIN).get();
    mailAuthInfoRepository.save(
        MailAuthInfo.builder()
            .user(user)
            .mail(schoolEmail)
            .school(school)
            .code(bCryptPasswordEncoder.encode(code))
            .build());

    VerifyEmailRequest request = new VerifyEmailRequest();
    request.setCode(code);

    String requestBody = objectMapper.writeValueAsString(request);

    // when
    ResultActions result = mockMvc.perform(
        post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    // then
    result.andExpect(status().isOk());
  }

  @DisplayName("leaveSchool(): 인증된 학교 정보를 삭제")
  @WithMockUser("user@mail.com")
  @Test
  public void successLeaveSchool() throws Exception {
    // given
    final String url = "/api/school";
    final String email = "user@mail.com";
    final String password = "user";
    final String schoolEmail = "test@" + TEST_SCHOOL_DOMAIN;

    User user = userService.save(new AddUserRequest(email, password, email));

    School school = schoolRepository.findByDomain(TEST_SCHOOL_DOMAIN).get();
    schoolMemberRepository.save(
        SchoolMember.builder()
            .school(school)
            .user(user)
            .mail(schoolEmail)
            .build());

    // when
    ResultActions result = mockMvc.perform(delete(url));

    // then
    result.andExpect(status().isOk());

    Optional<SchoolMember> member = schoolMemberRepository.findByUser(user);
    assertThat(member).isEmpty();
  }

  @DisplayName("getMySchool(): 자신의 학교 정보를 조회")
  @WithMockUser("user@mail.com")
  @Test
  public void successGetMySchool() throws Exception {
    // given
    final String url = "/api/school";
    final String email = "user@mail.com";
    final String password = "user";
    final String schoolEmail = "test@" + TEST_SCHOOL_DOMAIN;

    User user = userService.save(new AddUserRequest(email, password, email));

    School school = schoolRepository.findByDomain(TEST_SCHOOL_DOMAIN).get();
    schoolMemberRepository.save(
        SchoolMember.builder()
            .school(school)
            .user(user)
            .mail(schoolEmail)
            .build());

    // when
    ResultActions result = mockMvc.perform(get(url));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.schoolDomain").value(TEST_SCHOOL_DOMAIN))
        .andExpect(jsonPath("$.schoolName").value(TEST_SCHOOL_NAME))
        .andExpect(jsonPath("$.averageTime").isNotEmpty());
  }
}