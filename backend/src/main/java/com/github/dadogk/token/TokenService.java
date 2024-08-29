package com.github.dadogk.token;

import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.token.dto.AuthenticateRequest;
import com.github.dadogk.token.dto.AuthenticateResponse;
import com.github.dadogk.token.dto.TokenRequest;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.util.UserUtil;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TokenService {

  private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
  private static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

  private final TokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UserUtil userUtil;

  /**
   * 새로운 Access Token을 생성
   *
   * @param dto
   * @return
   */
  public String createNewAccessToken(TokenRequest dto) {
    // 토큰 유효성 검사에 실패하면 예외 발생
    if (!tokenProvider.validToken(dto.getRefreshToken().toString())) {
      throw new IllegalArgumentException("Unexpected token");
    }

    ////
    // TODO: access token이 비어있지 않은지 검증
    // TODO: access token이 본 서버에서 발급한 것이 맞는 지 검증
    // TODO: access token만 왔는지 검증을 통해 공격 차단 로직 위치
    ////

    Long userId = tokenProvider.getUserId(dto.getRefreshToken());
    User user = userUtil.findById(userId);

    return tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
  }

  /**
   * 새로운 Access Token과 Refresh Token을 생성
   *
   * @param dto
   * @return
   */
  public AuthenticateResponse createNewTokenSet(AuthenticateRequest dto) {
    // 유저의 이메일과 패스워드를 통해 유저를 확인
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        dto.getEmail(), dto.getPassword());
    Authentication authentication = authenticationManagerBuilder.getObject()
        .authenticate(authenticationToken);

    // 정상적으로 수행될 경우 user 객체 생성
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    User user = userUtil.findByEmail(userDetails.getUsername());

    // refresh token 생성
    String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
    // access token 생성
    String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

    UserResponse userResponse = userUtil.convertUserResponse(user);

    return new AuthenticateResponse(accessToken, refreshToken, userResponse);
  }
}
