package com.github.dadogk.socket;

import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.study.StudyConnectionHandler;
import com.github.dadogk.study.StudyService;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final StudyService studyService;
    private final TokenProvider tokenProvider;

    public WebSocketConfiguration(StudyService studyService, TokenProvider tokenProvider) {
        this.studyService = studyService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new StudyConnectionHandler(studyService, tokenProvider), "/study/connect");
    }
}
