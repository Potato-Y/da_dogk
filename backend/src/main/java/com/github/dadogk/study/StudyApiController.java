package com.github.dadogk.study;

import com.github.dadogk.study.dto.api.SubjectResponse;
import com.github.dadogk.study.dto.api.create.CreateSubjectRequest;
import com.github.dadogk.study.dto.api.recode.GetUserRecodesRequest;
import com.github.dadogk.study.dto.api.recode.RecodeResponse;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.study.mapper.StudyResponseMapper;
import com.github.dadogk.user.UserService;
import com.github.dadogk.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/study")
public class StudyApiController {

  private final UserService userService;
  private final StudyService studyService;
  private final StudyResponseMapper studyResponseMapper;

  @GetMapping("/subjects/{userId}") // 특정 사용자의 과목 리스트를 요청한다.
  public ResponseEntity<List<SubjectResponse>> getSubjectList(@PathVariable Long userId) {
    List<SubjectResponse> responses = studyService.getUserStudySubjectList(userId);

    return ResponseEntity.status(HttpStatus.OK)
        .body(responses);
  }

  @PostMapping("/subjects")
  public ResponseEntity<SubjectResponse> createSubject(
      @Validated @RequestBody CreateSubjectRequest request) {
    StudySubject subject = studyService.createSubject(request);
    SubjectResponse response = studyResponseMapper.convertSubjectResponse(subject);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }

  @DeleteMapping("/subjects/{subjectId}")
  public ResponseEntity<String> deleteSubject(@PathVariable Long subjectId) {
    studyService.deleteSubject(subjectId);

    return ResponseEntity.status(HttpStatus.OK)
        .build();
  }

  @GetMapping("/recodes")
  public ResponseEntity<List<RecodeResponse>> getCurrentUserRecodes(GetUserRecodesRequest request) {
    List<StudyRecord> records = studyService.getCurrentUserRecodes(request);
    List<RecodeResponse> recodeResponses = records.stream()
        .map(studyResponseMapper::convertRecodeResponse)
        .toList();

    return ResponseEntity.status(HttpStatus.OK)
        .body(recodeResponses);
  }

  @GetMapping("/recodes/{userId}")
  public ResponseEntity<List<RecodeResponse>> getUserRecodes(@PathVariable Long userId,
      GetUserRecodesRequest request) {
    User findUser = userService.findById(userId);
    List<StudyRecord> records = studyService.getUserRecodes(findUser, request);
    List<RecodeResponse> recodeResponses = records.stream()
        .map(studyResponseMapper::convertRecodeResponse)
        .toList();

    return ResponseEntity.status(HttpStatus.OK)
        .body(recodeResponses);
  }
}
