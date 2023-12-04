package com.github.dadogk.school;

import com.github.dadogk.school.dto.AuthMailRequest;
import com.github.dadogk.school.dto.VerifyEmailRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/school")
public class SchoolApiController {
    private final SchoolService schoolService;

    @PostMapping("/auth/mail")
    public ResponseEntity<String> requestAuthEmail(@Validated @RequestBody AuthMailRequest request) {
        schoolService.sendAuthCodeMail(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body("");
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<String> verifyEmail(@Validated @RequestBody VerifyEmailRequest request){
        schoolService.verifyEmail(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(request.getCode());
    }
}
