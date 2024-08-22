package com.github.dadogk.token;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dadogk.config.jwt.JwtProperties;
import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.token.dto.AuthenticateRequest;
import com.github.dadogk.token.dto.TokenRequest;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import com.github.dadogk.user.util.TestUserUtil;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TokenApiControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper; // JSON 직렬화, 역직렬화를 위한 클래스
  @Autowired
  private WebApplicationContext context;
  @Autowired
  JwtProperties jwtProperties;
  @Autowired
  UserRepository userRepository;
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  TokenProvider tokenProvider;

  TestUserUtil testUserUtil;

  @BeforeEach
  public void mockMvcSetup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    userRepository.deleteAll();
    this.testUserUtil = new TestUserUtil(bCryptPasswordEncoder, userRepository);
  }

  @DisplayName("authenticate(): 로그인 성공")
  @Test
  public void successAuthentication() throws Exception {
    // given 로그인에 필요한 객체들 생성
    final String url = "/api/authenticate";
    final String email = "user@mail.com";
    final String password = "test";

    testUserUtil.createTestUser(email, password);

    AuthenticateRequest request = new AuthenticateRequest();
    request.setEmail(email);
    request.setPassword(password);

    final String requestBody = objectMapper.writeValueAsString(request);

    // when 로그인에 요청
    ResultActions resultActions = mockMvc.perform(
        post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.refreshToken").isNotEmpty());
  }

  @DisplayName("token(): refresh token을 통해 새로운 access token 발급")
  @Test
  public void token() throws Exception {
    // given 토큰을 통한 로그인에 필요한 객체 생성
    final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    final String url = "/api/token";
    final String email = "user@mail.com";
    final String password = "test";

    User testUser = testUserUtil.createTestUser(email, password);

    // token set 생성
    final String accessToken = tokenProvider.generateToken(testUser, ACCESS_TOKEN_DURATION);
    final String refreshToken = tokenProvider.generateToken(testUser, REFRESH_TOKEN_DURATION);

    final TokenRequest tokenRequest = new TokenRequest();
    tokenRequest.setAccessToken(accessToken);
    tokenRequest.setRefreshToken(refreshToken);

    final String requestBody = objectMapper.writeValueAsString(tokenRequest);

    // when 새로운 access token 요청을 보낸다.
    ResultActions result = mockMvc.perform(
        post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    //then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isNotEmpty());
  }
}