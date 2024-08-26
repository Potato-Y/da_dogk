package com.github.dadogk.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class UserApiControllerTest {

  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper; // JSON 직렬화, 역직렬화를 위한 클래스
  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  UserRepository userRepository;

  @BeforeEach
  public void mockMvcSetup() {
    userRepository.deleteAll();
  }

  @DisplayName("addUser: 새로운 유저 추가에 성공")
  @Test
  public void addUser() throws Exception {
    // given 새로운 유저 추가에 필요한 객체 생성
    final String url = "/api/signup";
    final String email = "test@mail.com";
    final String password = "test";
    final String nickname = "test user";
    final AddUserRequest dto = new AddUserRequest(email, password, nickname);

    // 객체 JSON으로 직렬화
    final String requestBody = objectMapper.writeValueAsString(dto);

    // when 회원가입 요청을 보낸다.
    ResultActions result =
        mockMvc.perform(
            post(url).contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody));

    // then 응답코드가 201인지 확인
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.nickname").value(nickname));

    // then
    Optional<User> savedUser = userRepository.findByEmail(email);

    assertThat(savedUser).isPresent();
    assertThat(savedUser.get().getEmail()).isEqualTo(email);
    assertThat(savedUser.get().getNickname()).isEqualTo(nickname);
    assertThat(bCryptPasswordEncoder.matches(password, savedUser.get().getPassword())).isTrue();
  }
}