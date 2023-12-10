package com.github.dadogk.study;

import com.github.dadogk.study.dto.api.SubjectTodayTime;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.util.UserUtil;
import java.util.ArrayList;
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

import com.github.dadogk.study.dto.api.SubjectTitleResponse;
import com.github.dadogk.study.dto.api.create.CreateSubjectRequest;
import com.github.dadogk.study.dto.api.recode.GetUserRecodesRequest;
import com.github.dadogk.study.dto.api.recode.RecodeResponse;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.study.util.StudyUtil;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/study")
public class StudyApiController {
    private final StudyService studyService;
    private final StudyUtil studyUtil;
    private final UserUtil userUtil;

    @GetMapping("/subjects/{userId}") // 특정 사용자의 과목 리스트를 요청한다.
    public ResponseEntity<List<SubjectTitleResponse>> getSubjectList(@PathVariable Long userId) {
        List<SubjectTitleResponse> responses = studyService.getUserStudySubjectList(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responses);
    }

    @PostMapping("/subjects")
    public ResponseEntity<SubjectTitleResponse> createSubject(@Validated @RequestBody CreateSubjectRequest request) {
        StudySubject subject = studyService.createSubject(request);
        SubjectTitleResponse response = studyUtil.convertSubjectToTitleResponse(subject);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/subjects/{subjectId}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long subjectId) {
        studyService.deleteSubject(subjectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/recodes")
    public ResponseEntity<List<RecodeResponse>> getCurrentUserRecodes(GetUserRecodesRequest request) {
        List<StudyRecord> records = studyService.getCurrentUserRecodes(request);
        List<RecodeResponse> recodeResponses = new ArrayList<>();
        for (StudyRecord record : records) {
            recodeResponses.add(studyUtil.convertRecodeResponse(record));

        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(recodeResponses);
    }

    @GetMapping("/recodes/{userId}")
    public ResponseEntity<List<RecodeResponse>> getUserRecodes(@PathVariable Long userId,
                                                               GetUserRecodesRequest request) {
        User findUser = userUtil.findById(userId);
        List<StudyRecord> records = studyService.getUserRecodes(findUser, request);
        List<RecodeResponse> recodeResponses = new ArrayList<>();
        for (StudyRecord record : records) {
            recodeResponses.add(studyUtil.convertRecodeResponse(record));

        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(recodeResponses);
    }

    @GetMapping("/subjects/{subjectId}/time") // 특정 과목 당일 전체 공부 시간
    public ResponseEntity<SubjectTodayTime> getSubjectTodayTime(@PathVariable Long subjectId) {
        Long totalTime = studyService.getSubjectTodayTime(subjectId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SubjectTodayTime(totalTime));
    }
}
