package com.github.dadogk.school;

import com.github.dadogk.school.dto.AuthMailRequest;
import com.github.dadogk.school.dto.SchoolInfoResponse;
import com.github.dadogk.school.dto.VerifyEmailRequest;
import com.github.dadogk.school.entity.SchoolMember;
import com.github.dadogk.school.util.SchoolUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/school")
public class SchoolApiController {
    private final SchoolService schoolService;
    private final SchoolUtil schoolUtil;

    @PostMapping("/auth/mail")
    public ResponseEntity<String> requestAuthEmail(@Validated @RequestBody AuthMailRequest request) {
        schoolService.sendAuthCodeMail(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body("");
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<String> verifyEmail(@Validated @RequestBody VerifyEmailRequest request) {
        schoolService.verifyEmail(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body("");
    }

    @DeleteMapping("") // school 인증 삭제
    public ResponseEntity<String> leaveSchool() {
        schoolService.leaveSchool();

        return ResponseEntity.status(HttpStatus.OK)
                .body("");
    }

    @GetMapping("") // 가입한 학교 정보 가져오기
    public ResponseEntity<SchoolInfoResponse> getMySchool() {
        SchoolMember schoolMember = schoolService.getMySchool();

        return ResponseEntity.status(HttpStatus.OK)
                .body(schoolUtil.convertSchoolInfo(schoolMember));
    }
}
