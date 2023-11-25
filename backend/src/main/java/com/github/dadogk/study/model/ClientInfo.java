package com.github.dadogk.study.model;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import com.github.dadogk.study.entity.StudyRecord;

@Getter
public class ClientInfo {
    WebSocketSession session;
    /**
     * DB User ID
     */
    private final Long userId;
    /**
     * 현재 학습 중인 공부 정보
     */
    private final StudyRecord studyRecord;

    public ClientInfo(WebSocketSession session, Long userId, StudyRecord studyRecord) {
        this.session = session;
        this.userId = userId;
        this.studyRecord = studyRecord;
    }
}
