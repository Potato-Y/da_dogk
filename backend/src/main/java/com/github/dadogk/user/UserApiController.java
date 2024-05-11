package com.github.dadogk.user;

import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.user.dto.AddUserRequest;
import com.github.dadogk.user.dto.AddUserResponse;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserApiController {

  private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);
  private final UserService userService;
  private final SecurityUtil securityUtil;
  private final UserUtil userUtil;

  @PostMapping("/signup")
  public ResponseEntity<AddUserResponse> signup(@Validated @RequestBody AddUserRequest request) {
    User user = userService.save(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AddUserResponse(user.getId(), user.getEmail(), user.getNickname()));
  }

  @GetMapping("/user")
  public ResponseEntity<UserResponse> user() {
    User user = securityUtil.getCurrentUser();

    logger.info("user. userId={}", user.getId());

    return ResponseEntity.status(HttpStatus.OK)
        .body(userUtil.convertUserResponse(user));
  }

  @DeleteMapping("/user")
  public ResponseEntity<String> deleteUser() {
    userService.deleteUser();

    return ResponseEntity.status(HttpStatus.OK)
        .body(null);

  }

  // TODO: 특정 유저 조회
}
