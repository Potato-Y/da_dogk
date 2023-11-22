package com.github.dadogk.studytracker;

import com.github.dadogk.studytracker.dto.api.SubjectTitleResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/study")
@Log4j2
public class StudyApiController {
    private final StudyService studyService;

    @GetMapping("/subject_list/{userId}") // 특정 사용자의 과목 리스트를 요청한다.
    public ResponseEntity<List<SubjectTitleResponse>> getSubjectList(@PathVariable Long userId) {
        List<SubjectTitleResponse> responses = studyService.getUserStudySubjectList(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(responses);
    }
}
