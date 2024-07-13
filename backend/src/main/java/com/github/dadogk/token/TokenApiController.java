package com.github.dadogk.token;

import com.github.dadogk.token.dto.AuthenticateRequest;
import com.github.dadogk.token.dto.AuthenticateResponse;
import com.github.dadogk.token.dto.TokenRequest;
import com.github.dadogk.token.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TokenApiController {

  private final TokenService tokenService;

  @PostMapping("/authenticate") // 새로운 토큰 세트 생성
  public ResponseEntity<AuthenticateResponse> authenticate(
      @RequestBody AuthenticateRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(tokenService.createNewTokenSet(request));
  }

  @PostMapping("/token") // refresh 토큰을 통해 access 토큰 재발급
  public ResponseEntity<TokenResponse> token(@RequestBody TokenRequest request) {
    String accessToken = tokenService.createNewAccessToken(request);

    return ResponseEntity.status(HttpStatus.OK)
        .body(new TokenResponse(accessToken));
  }
}
