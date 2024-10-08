package com.github.dadogk.school;

import com.github.dadogk.error.exception.NotFoundException;
import com.github.dadogk.exceptions.DuplicatedException;
import com.github.dadogk.school.dto.AuthMailRequest;
import com.github.dadogk.school.dto.VerifyEmailRequest;
import com.github.dadogk.school.entity.MailAuthInfo;
import com.github.dadogk.school.entity.MailAuthInfoRepository;
import com.github.dadogk.school.entity.School;
import com.github.dadogk.school.entity.SchoolMember;
import com.github.dadogk.school.entity.SchoolMemberRepository;
import com.github.dadogk.school.entity.SchoolRepository;
import com.github.dadogk.school.exception.SchoolMailDuplicatedException;
import com.github.dadogk.security.CurrentUserProvider;
import com.github.dadogk.security.PasswordEncryptionService;
import com.github.dadogk.security.authentication.VerificationCodeGenerator;
import com.github.dadogk.security.exception.PasswordIncorrectException;
import com.github.dadogk.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolService {

  // application.yml에 smtp 정보가 입력되지 않아 밑줄이 표시되나 active profiles에 명시됨
  private final JavaMailSender javaMailSender;
  private final SchoolRepository schoolRepository;
  private final SchoolMemberRepository schoolMemberRepository;
  private final MailAuthInfoRepository mailAuthInfoRepository;
  private final PasswordEncryptionService passwordEncryptionService;
  private final CurrentUserProvider currentUserProvider;

  @Transactional
  public void sendAuthCodeMail(AuthMailRequest dto) {
    User user = currentUserProvider.getCurrentUser();

    Optional<SchoolMember> schoolMember = schoolMemberRepository.findByUser(user);
    if (schoolMember.isPresent()) {
      log.warn("sendAuthCodeMail: 이미 가입된 그룹이 있습니다. userId={}, mail={}", user.getId(),
          dto.getEmail());
      throw new DuplicatedException("중복 가입 요청");
    }

    try {
      // 학교 정보 가져오기, 코드 저장
      School school = findSchool(dto.getEmail());
      validateMailDuplicated(dto.getEmail());
      String code = VerificationCodeGenerator.createCode();
      saveAuthInfo(user, school, dto.getEmail(), code);

      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
      mimeMessageHelper.setTo(dto.getEmail());
      mimeMessageHelper.setFrom("service@dadogk2.duckdns.org");
      mimeMessageHelper.setSubject("다독: 대학생 이메일 인증 코드입니다.");
      mimeMessageHelper.setText("인증 코드: " + code + "\n본 메일 주소는 발송용입니다.");
      javaMailSender.send(mimeMessage);
    } catch (SchoolMailDuplicatedException e) {
      log.warn("sendMail: Duplicated mail. userId={}, email={}", user.getId(), dto.getEmail());
      throw new SchoolMailDuplicatedException(e.getMessage());
    } catch (NotFoundException e) {
      log.warn("findSchool: Not found mail. userId={}, email={}", user.getId(), dto.getEmail());
      throw new NotFoundException(e.getMessage());
    } catch (MessagingException e) {
      log.warn("sendMail: Failed send mail. userId={}, email={}", user.getId(), dto.getEmail());
      throw new MailSendException("이메일 전송 실패");
    } catch (DataIntegrityViolationException e) {
      log.warn("sendMail: Duplicated mail. userId={}, email={}", user.getId(), dto.getEmail());
      throw new DuplicatedException("인증 정보 중복 저장 요청");
    }
  }

  private void validateMailDuplicated(String mail) {
    Optional<SchoolMember> member = schoolMemberRepository.findByMail(mail);

    if (member.isPresent()) {
      throw new SchoolMailDuplicatedException("이미 인증된 메일입니다.");
    }
  }

  /**
   * 인증 정보를 추가한다.
   *
   * @param user   대상 사용자
   * @param school 대상 학교
   * @param code   인증 코드
   */
  @Transactional
  public void saveAuthInfo(User user, School school, String mail, String code) {
    Optional<MailAuthInfo> mailAuthCode = mailAuthInfoRepository.findByUser(user);
    if (mailAuthCode.isEmpty()) {
      MailAuthInfo createInfo = MailAuthInfo.builder().user(user).school(school).mail(mail)
          .code(passwordEncryptionService.encryptPassword(code)).build();
      mailAuthInfoRepository.save(createInfo);

      return;
    }

    mailAuthInfoRepository.save(
        mailAuthCode.get()
            .updateNewAuth(school, mail, passwordEncryptionService.encryptPassword(code)));
  }

  /**
   * 유저가 입력한 이메일을 사용하는 학교를 찾는다.
   *
   * @param email 사용자 이메일
   * @return 학교 엔티티
   */
  private School findSchool(String email) {
    String domain = email.split("@")[1]; // @를 기점으로 뒷 내용 가져오기

    Optional<School> school = schoolRepository.findByDomain(domain);
    if (school.isEmpty()) {
      throw new NotFoundException("이메일이 없음");
    }

    return school.get();
  }

  @Transactional
  public void verifyEmail(VerifyEmailRequest dto) {
    User user = currentUserProvider.getCurrentUser();
    Optional<MailAuthInfo> mailAuthCode = mailAuthInfoRepository.findByUser(user);

    if (mailAuthCode.isEmpty()) {
      log.warn("verifyEmail: Not Found mail auth info. userId={}", user.getId());
      throw new NotFoundException("요청한 인증 정보가 없음");
    }

    boolean result = passwordEncryptionService.verifyPassword(dto.getCode(),
        mailAuthCode.get().getCode());
    if (!result) {
      log.warn("verifyEmail: Not match code. userId={}, school={}", user.getId(),
          mailAuthCode.get().getSchool().getDomain());
      mailAuthInfoRepository.delete(mailAuthCode.get()); // 인증 정보 삭제

      throw new PasswordIncorrectException("인증 코드가 동일하지 않음");
    }

    SchoolMember schoolMember = SchoolMember.builder().school(mailAuthCode.get().getSchool())
        .user(user).mail(mailAuthCode.get().getMail()).build();
    schoolMemberRepository.save(schoolMember);

    mailAuthInfoRepository.delete(mailAuthCode.get()); // 인증 정보 삭제
  }

  @Transactional
  public void leaveSchool() {
    User user = currentUserProvider.getCurrentUser();

    Optional<SchoolMember> schoolMember = schoolMemberRepository.findByUser(user);
    if (schoolMember.isEmpty()) {
      log.warn("leaveSchool: Not found school member. userId={}", user.getId());
      throw new NotFoundException("인증한 학교가 없음");
    }

    schoolMemberRepository.delete(schoolMember.get());
  }

  @Transactional(readOnly = true)
  public SchoolMember getMySchool() {
    User user = currentUserProvider.getCurrentUser();
    Optional<SchoolMember> schoolMember = schoolMemberRepository.findByUser(user);
    if (schoolMember.isEmpty()) {
      log.warn("getMySchool: Not found school member. userId={}", user.getId());
      throw new NotFoundException("학교 정보를 찾을 수 없음.");
    }

    return schoolMember.get();
  }
}
