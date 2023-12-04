package com.github.dadogk.school;

import com.github.dadogk.error.exception.NotFoundException;
import com.github.dadogk.school.dto.AuthMailRequest;
import com.github.dadogk.school.entity.MailAuthCode;
import com.github.dadogk.school.entity.MailAuthCodeRepository;
import com.github.dadogk.school.entity.School;
import com.github.dadogk.school.entity.SchoolRepository;
import com.github.dadogk.security.util.CodeMaker;
import com.github.dadogk.security.util.PasswordUtil;
import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class SchoolService {
    private final JavaMailSender javaMailSender;
    private final SchoolRepository schoolRepository;
    private final MailAuthCodeRepository mailAuthCodeRepository;
    private final PasswordUtil passwordUtil;
    private final SecurityUtil securityUtil;

    public void sendAuthCodeMail(AuthMailRequest dto) {
        User user = securityUtil.getCurrentUser();

        try {
            // 학교 정보 가져오기, 코드 저장
            School school = findSchool(dto.getEmail());
            String code = CodeMaker.createCode();
            saveAuthInfo(user, school, code);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(dto.getEmail());
            mimeMessageHelper.setFrom("service@dadogk2.duckdns.org");
            mimeMessageHelper.setSubject("다독: 대학생 이메일 인증 코드입니다.");
            mimeMessageHelper.setText("인증 코드: " + code + "\n본 메일 주소는 발송용입니다.");
            javaMailSender.send(mimeMessage);
        } catch (NotFoundException e) {
            log.warn("findSchool: Not found mail. userId={}, email={}", user.getId(), dto.getEmail());
            throw new NotFoundException(e.getMessage());
        } catch (MessagingException e) {
            log.warn("sendMail: Failed send mail. userId={}, email={}", user.getId(), dto.getEmail());
            throw new MailSendException("이메일 전송 실패");
        }
    }

    /**
     * 인증 정보를 추가한다.
     *
     * @param user
     * @param school
     * @param code
     */
    private void saveAuthInfo(User user, School school, String code) {
        mailAuthCodeRepository.save(MailAuthCode.builder()
                .user(user)
                .school(school)
                .code(passwordUtil.convertPassword(code))
                .build());
    }

    /**
     * 유저가 입력한 이메일을 사용하는 학교를 찾는다.
     *
     * @param email
     * @return
     */
    private School findSchool(String email) {
        String domain = email.split("@")[1]; // @를 기점으로 뒷 내용 가져오기

        Optional<School> school = schoolRepository.findByDomain(domain);
        if (school.isEmpty()) {
            throw new NotFoundException("이메일이 없음");
        }

        return school.get();
    }
}
