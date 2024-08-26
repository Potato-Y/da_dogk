package com.github.dadogk.user.util;

import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@AllArgsConstructor
public class TestUserUtil {

  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private UserRepository userRepository;
  
  public User createTestUser(String email, String password) {

    return userRepository.save(
        User.builder()
            .email(email)
            .password(bCryptPasswordEncoder.encode(password))
            .nickname(email)
            .build());
  }
}
