package com.github.dadogk.user;

import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.user.dto.AddUserDto;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import com.github.dadogk.user.event.UserCreateEvent;
import com.github.dadogk.user.exception.NotFoundUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final SecurityUtil securityUtil;
  private final ApplicationEventPublisher publisher;

  public User findById(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundUserException("Unexpected user"));
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundUserException("Unexpected user"));
  }

  @Transactional
  public User save(AddUserDto.AddUserRequest dto) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    User user = userRepository.save(User.builder()
        .email(dto.getEmail())
        .password(encoder.encode(dto.getPassword()))
        .nickname(dto.getNickname())
        .build());

    publisher.publishEvent(new UserCreateEvent(user)); // 과목 기본 설정
    return user;
  }

  @Transactional
  public void deleteUser() {
    User user = securityUtil.getCurrentUser();
    userRepository.delete(user);
  }
}
