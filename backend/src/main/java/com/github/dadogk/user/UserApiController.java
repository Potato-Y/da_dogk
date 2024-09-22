package com.github.dadogk.user;

import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.user.dto.AddUserDto;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.mapper.UserResponseMapper;
import lombok.RequiredArgsConstructor;
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

  private final UserService userService;
  private final SecurityUtil securityUtil;
  private final UserResponseMapper userResponseMapper;

  @PostMapping("/signup")
  public ResponseEntity<AddUserDto.AddUserResponse> signup(
      @Validated @RequestBody AddUserDto.AddUserRequest request) {
    User user = userService.save(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new AddUserDto.AddUserResponse(user.getId(), user.getEmail(), user.getNickname()));
  }

  @GetMapping("/user")
  public ResponseEntity<UserResponse> user() {
    User user = securityUtil.getCurrentUser();

    return ResponseEntity.status(HttpStatus.OK)
        .body(userResponseMapper.convertUserResponse(user));
  }

  @DeleteMapping("/user")
  public ResponseEntity<String> deleteUser() {
    userService.deleteUser();

    return ResponseEntity.status(HttpStatus.OK).build();

  }

  // TODO: 특정 유저 조회
}
