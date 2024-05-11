package com.github.dadogk.security.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

  private final BCryptPasswordEncoder passwordEncoder;

  public String convertPassword(String password) {
    return passwordEncoder.encode(password);
  }

  public boolean matches(String inputPassword, String password) {
    return passwordEncoder.matches(inputPassword, password);
  }
}
