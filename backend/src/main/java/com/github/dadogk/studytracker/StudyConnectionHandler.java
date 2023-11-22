package com.github.dadogk.studytracker;

import static com.github.dadogk.studytracker.model.ResponseMessage.ALREADY_PROCESSED;
import static com.github.dadogk.studytracker.model.ResponseMessage.SUBJECT_ID_ERROR;
import static com.github.dadogk.studytracker.model.ResponseMessage.SUCCESS_PROCESSED;
import static com.github.dadogk.studytracker.model.ResponseMessage.TOKEN_ERROR;
import static com.github.dadogk.studytracker.model.ResponseType.FAIL;
import static com.github.dadogk.studytracker.model.ResponseType.FORBIDDEN;
import static com.github.dadogk.studytracker.model.ResponseType.NOT_FOUND;
import static com.github.dadogk.studytracker.model.ResponseType.OK;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.studytracker.dto.StudyStartRequest;
import com.github.dadogk.studytracker.dto.socket.SimpleResponse;
import com.github.dadogk.studytracker.entity.StudyRecord;
import com.github.dadogk.studytracker.entity.StudySubject;
import com.github.dadogk.studytracker.model.ClientInfo;
import com.github.dadogk.studytracker.model.RequestType;
import com.github.dadogk.user.UserService;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@RequiredArgsConstructor
@Log4j2
public class StudyConnectionHandler extends AbstractWebSocketHandler {
    private static final Map<String, ClientInfo> CLIENTS = Collections.synchronizedMap(new HashMap<>());
    private static final MultiValueMap<Long, String> GROUP_MEMBERS = new LinkedMultiValueMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;
    private final StudyService studyService;
    private final TokenProvider tokenProvider;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());
            RequestType requestType = RequestType.valueOf(jsonNode.get("type").asText()); // 메시지 타입을 가져온다.

            if (requestType == RequestType.STUDY_START) {
                studyStart(session, objectMapper.treeToValue(jsonNode, StudyStartRequest.class));
                return;
            }
            if (requestType == RequestType.GET_GROUP_INFO) {
            }
        } catch (IllegalArgumentException e) {
            log.warn("handleTextMessage. NotFound MessageType session={}, message={}", session, message.getPayload());
        } catch (NullPointerException e) {
            log.warn("handleTextMessage. NotFound Type Node session={}, message={}", session, message.getPayload());
        }
    }

    /**
     * 공부 시작에 대한 로직 시작
     *
     * @param session 사용자 세션
     * @param dto     StudyStartRequest
     */
    private void studyStart(WebSocketSession session, StudyStartRequest dto) throws IOException {
        if (CLIENTS.containsKey(session.getId())) {
            sendSimpleMessage(session, new SimpleResponse(FAIL, ALREADY_PROCESSED));
            return; // 만약 이미 CLIENT에 있는 Session 이라면 진행하지 않는다.
        }

        // Token 검증
        if (!tokenProvider.validToken(dto.getAccessToken())) {
            sendSimpleMessage(session, new SimpleResponse(FORBIDDEN, TOKEN_ERROR));
            session.close();
        }

        Long userId = tokenProvider.getUserId(dto.getAccessToken());
        try {
            StudySubject subject = studyService.getSubject(dto.getSubjectId(), userId);
            StudyRecord studyRecord = studyService.startStudy(subject);

            CLIENTS.put(session.getId(), new ClientInfo(session, userId, studyRecord));
            sendSimpleMessage(session, new SimpleResponse(OK, SUCCESS_PROCESSED));
            log.info("studyStart. Start Study. sessionId={}", session.getId());
        } catch (IllegalArgumentException e) {
            sendSimpleMessage(session, new SimpleResponse(NOT_FOUND, SUBJECT_ID_ERROR));
            session.close();
        }
    }

    /**
     * 사용자가 접속을 종료하면 공부를 끝내고 아이템을 정리한다.
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (!CLIENTS.containsKey(session.getId())) {
            log.warn("afterConnectionClosed. Not found item. sessionId={}", session.getId());
            return;
        }
        ClientInfo clientInfo = CLIENTS.get(session.getId());
        studyService.endStudy(clientInfo.getStudyRecord());

        CLIENTS.remove(session.getId());
        log.info("afterConnectionClosed. Close session. sessionId={}", session.getId());
    }

    private void sendSimpleMessage(WebSocketSession session, SimpleResponse dto) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
    }
}
