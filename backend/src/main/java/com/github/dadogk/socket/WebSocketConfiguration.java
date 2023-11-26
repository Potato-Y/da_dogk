package com.github.dadogk.socket;

import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.study.StudyConnectionHandler;
import com.github.dadogk.study.StudyService;
import com.github.dadogk.user.util.UserUtil;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final StudyService studyService;
    private final TokenProvider tokenProvider;
    private final UserUtil userUtil;

    public WebSocketConfiguration(StudyService studyService, TokenProvider tokenProvider, UserUtil userUtil) {
        this.studyService = studyService;
        this.tokenProvider = tokenProvider;
        this.userUtil = userUtil;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new StudyConnectionHandler(studyService, tokenProvider, userUtil), "/study/connect");
    }
}
