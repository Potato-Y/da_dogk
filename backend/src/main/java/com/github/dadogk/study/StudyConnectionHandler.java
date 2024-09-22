package com.github.dadogk.study;

import static com.github.dadogk.study.model.ResponseMessage.ALREADY_PROCESSED;
import static com.github.dadogk.study.model.ResponseMessage.NOT_FOUND_SESSION;
import static com.github.dadogk.study.model.ResponseMessage.SUBJECT_ID_ERROR;
import static com.github.dadogk.study.model.ResponseMessage.SUCCESS_PROCESSED;
import static com.github.dadogk.study.model.ResponseMessage.TOKEN_ERROR;
import static com.github.dadogk.study.model.ResponseType.FAIL;
import static com.github.dadogk.study.model.ResponseType.FORBIDDEN;
import static com.github.dadogk.study.model.ResponseType.NOT_FOUND;
import static com.github.dadogk.study.model.ResponseType.OK;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dadogk.config.jwt.TokenProvider;
import com.github.dadogk.error.exception.NotFoundException;
import com.github.dadogk.group.entity.GroupMember;
import com.github.dadogk.study.dto.StudyStartRequest;
import com.github.dadogk.study.dto.socket.CloseGroupMemberResponse;
import com.github.dadogk.study.dto.socket.ConnectingGroupMembersResponse;
import com.github.dadogk.study.dto.socket.GroupMembersResponse;
import com.github.dadogk.study.dto.socket.SimpleResponse;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.study.model.ClientInfo;
import com.github.dadogk.study.model.RequestType;
import com.github.dadogk.user.UserService;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.mapper.UserResponseMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
public class StudyConnectionHandler extends AbstractWebSocketHandler {

  private static final Map<String, ClientInfo> CLIENTS = Collections.synchronizedMap(
      new HashMap<>());
  private static final Map<Long, List<String>> GROUP_MEMBERS = new ConcurrentReferenceHashMap<>();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final UserService userService;
  private final StudyService studyService;
  private final TokenProvider tokenProvider;
  private final UserResponseMapper userResponseMapper;

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
        getConnectingGroupMember(session);
      }
    } catch (IllegalArgumentException e) {
      log.warn("handleTextMessage. NotFound MessageType session={}, message={}", session,
          message.getPayload());
    } catch (NullPointerException e) {
      log.warn("handleTextMessage. NotFound Type Node session={}, message={}", session,
          message.getPayload());
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
      User user = userService.findById(userId);
      StudySubject subject = studyService.getSubject(dto.getSubjectId(), userId);
      StudyRecord studyRecord = studyService.startStudy(user, subject);

      CLIENTS.put(session.getId(),
          new ClientInfo(session, userId, studyRecord, user.getGroupMembers()));
      List<GroupMember> groupMembers = user.getGroupMembers();
      for (GroupMember groupMember : groupMembers) {
        GROUP_MEMBERS.computeIfAbsent(groupMember.getGroup().getId(),
            k -> Collections.synchronizedList(new ArrayList<>())).add(session.getId());
      }

      sendSimpleMessage(session, new SimpleResponse(OK, SUCCESS_PROCESSED));
    } catch (NotFoundException e) {
      sendSimpleMessage(session, new SimpleResponse(NOT_FOUND, e.getMessage()));
      session.close();
    } catch (IllegalArgumentException e) {
      sendSimpleMessage(session, new SimpleResponse(NOT_FOUND, SUBJECT_ID_ERROR));
      session.close();
    }
  }

  private void getConnectingGroupMember(WebSocketSession session) throws IOException {
    ClientInfo clientInfo = CLIENTS.get(session.getId());
    if (clientInfo == null) { // 클라이언트 정보가 없다면 정상 진입이 아닌 것으로 판단
      sendSimpleMessage(session, new SimpleResponse(NOT_FOUND, NOT_FOUND_SESSION));
    }

    ConnectingGroupMembersResponse responseDto = new ConnectingGroupMembersResponse();

    List<Long> groupIds = clientInfo.getGroups(); // 클라이언트 정보에서 가입한 그룹을 가져온다.
    for (Long groupId : groupIds) {
      if (!GROUP_MEMBERS.containsKey(groupId)) { // 만약 연결한 그룹원이 아무도 없을 경우 건너뛴다.
        continue;
      }

      List<UserResponse> userResponses = new ArrayList<>();
      List<String> groupMemberSessionIds = GROUP_MEMBERS.get(groupId); // 그룹 id로 그룹원 세션 id를 가져온다.
      for (String memberSessionId : groupMemberSessionIds) {
        ClientInfo info = CLIENTS.get(memberSessionId); // 해당 클라이언트의 정보를 가져온다.
        User user = userService.findById(info.getUserId()); // 유저 객체를 가져온다.
        userResponses.add(userResponseMapper.convertUserResponse(user));
      }
      responseDto.addGroupMembers(new GroupMembersResponse(groupId, userResponses));
    }

    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseDto)));
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

    broadcastClosedUserForGroup(session);

    CLIENTS.remove(session.getId());
    // 세션 ID로 찾아서 모든 리스트에서 삭제
    for (Map.Entry<Long, List<String>> entry : GROUP_MEMBERS.entrySet()) {
      List<String> sessionsList = entry.getValue();
      sessionsList.removeIf(valueSession -> valueSession.equals(session.getId()));
    }
  }

  private void broadcastClosedUserForGroup(WebSocketSession session) throws IOException {
    ClientInfo clientInfo = CLIENTS.get(session.getId());
    User closeUser = userService.findById(clientInfo.getUserId());
    List<Long> groupIds = clientInfo.getGroups();
    for (Long groupId : groupIds) {
      List<String> groupMemberSessionIds = GROUP_MEMBERS.get(groupId);
      for (String sessionId : groupMemberSessionIds) {
        if (session.getId() == sessionId) { // 본인은 건너뛴다.
          continue;
        }

        WebSocketSession memberSession = CLIENTS.get(sessionId).getSession();
        CloseGroupMemberResponse dto = new CloseGroupMemberResponse(groupId,
            userResponseMapper.convertUserResponse(closeUser));
        memberSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
      }
    }
  }

  private void sendSimpleMessage(WebSocketSession session, SimpleResponse dto) throws IOException {
    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(dto)));
  }
}
