package com.github.dadogk.socket;

import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.studytracker.StudyConnectionHandler;
import com.github.dadogk.studytracker.StudyService;
import com.github.dadogk.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final UserService userService;
    private final StudyService studyService;
    private final TokenProvider tokenProvider;

    @Autowired
    public WebSocketConfiguration(UserService userService, StudyService studyService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.studyService = studyService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new StudyConnectionHandler(userService, studyService, tokenProvider), "/study/connect");
    }
}
