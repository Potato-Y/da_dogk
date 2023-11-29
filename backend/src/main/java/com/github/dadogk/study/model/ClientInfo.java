package com.github.dadogk.study.model;

import com.github.dadogk.group.entity.GroupMember;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import com.github.dadogk.study.entity.StudyRecord;

@Getter
public class ClientInfo {
    List<Long> groups= new ArrayList<>();
    WebSocketSession session;
    /**
     * DB User ID
     */
    private final Long userId;
    /**
     * 현재 학습 중인 공부 정보
     */
    private final StudyRecord studyRecord;

    public ClientInfo(WebSocketSession session, Long userId, StudyRecord studyRecord, List<GroupMember> groupMembers) {
        this.session = session;
        this.userId = userId;
        this.studyRecord = studyRecord;

        for (GroupMember groupMember :                groupMembers) {
            groups.add(groupMember.getGroup().getId());
        }
    }
}
