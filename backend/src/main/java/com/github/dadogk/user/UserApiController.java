package com.github.dadogk.user;

import com.github.dadogk.config.jwt.JwtProperties;
import com.github.dadogk.user.dto.AddUserRequest;
import com.github.dadogk.user.dto.AddUserResponse;
import com.github.dadogk.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<AddUserResponse> signup(@Validated @RequestBody AddUserRequest request) {
        User user = userService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AddUserResponse(user.getId(), user.getEmail(), user.getNickname()));
    }

    // TODO: 특정 유저 조회
}
