package com.github.dadogk.socket;

import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.study.StudyConnectionHandler;
import com.github.dadogk.study.StudyService;
import com.github.dadogk.user.UserService;
import com.github.dadogk.user.mapper.UserResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

  private final UserService userService;
  private final StudyService studyService;
  private final TokenProvider tokenProvider;
  private final UserResponseMapper userResponseMapper;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(
        new StudyConnectionHandler(userService, studyService, tokenProvider, userResponseMapper),
        "/study/connect");
  }
}
