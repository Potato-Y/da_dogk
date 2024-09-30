package com.github.dadogk.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncryptionService {

  private final BCryptPasswordEncoder passwordEncoder;

  public String encryptPassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  public boolean verifyPassword(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
