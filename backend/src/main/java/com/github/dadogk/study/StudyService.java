package com.github.dadogk.study;

import static com.github.dadogk.common.constants.DateTimeConstants.END_OF_DAY;
import static com.github.dadogk.common.constants.DateTimeConstants.FIRST_DAY_OF_MONTH;

import com.github.dadogk.common.utils.DateTimeUtil;
import com.github.dadogk.exceptions.PermissionException;
import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.study.dto.api.SubjectResponse;
import com.github.dadogk.study.dto.api.create.CreateSubjectRequest;
import com.github.dadogk.study.dto.api.recode.GetUserRecodesRequest;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudyRecordRepository;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.study.entity.StudySubjectRepository;
import com.github.dadogk.study.exception.NotFoundStudyException;
import com.github.dadogk.study.mapper.StudyResponseMapper;
import com.github.dadogk.user.UserService;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.event.UserCreateEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudyService {

  @Value("${default.subject.title}")
  private String defaultSubjectTitle;

  private final StudySubjectRepository subjectRepository;
  private final StudyRecordRepository studyRecordRepository;
  private final UserService userService;
  private final StudyResponseMapper studyResponseMapper;
  private final SecurityUtil securityUtil;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleUserCreatedEvent(UserCreateEvent event) {
    defaultSetting(event.user());
  }

  /**
   * 과목 기본 설정을 한다.
   *
   * @param user 유저 객체
   */
  @Transactional
  public void defaultSetting(User user) {
    StudySubject subject = StudySubject.builder()
        .title(defaultSubjectTitle)
        .user(user)
        .build();

    subjectRepository.save(subject);
  }

  @Transactional(readOnly = true)
  public StudySubject getSubject(Long subjectId, Long userId) {
    Optional<StudySubject> subject = subjectRepository.findById(subjectId);
    if (subject.isEmpty()) {
      log.warn("getSubject. 잘못된 StudySubject id={}", subjectId);
      throw new IllegalArgumentException("잘못된 StudySubject id");
    }

    validateSubjectUserMatch(subject.get(), userId);

    return subject.get();
  }

  private void validateSubjectUserMatch(StudySubject subject, Long userId) {
    if (subject.getUser().getId().equals(userId)) {
      return;
    }

    log.warn("validateSubjectUserMatch: User와 Subject User가 동일하지 않음. userId={}, subjectId={}",
        userId,
        subject.getId());
    throw new IllegalArgumentException("User와 Subject user가 맞지 않음");
  }

  @Transactional
  public StudyRecord startStudy(User user, StudySubject subject) {
    return studyRecordRepository.save(StudyRecord.builder()
        .user(user)
        .subject(subject)
        .build());
  }

  @Transactional
  public void endStudy(StudyRecord record) {
    studyRecordRepository.save(record.updateEndAt());
  }

  /**
   * 특정 유저의 과목 리스트를 불러온다.
   *
   * @param userId 조회하려는 UserId
   * @return List<SubjectTitleResponse>
   */
  @Transactional(readOnly = true)
  public List<SubjectResponse> getUserStudySubjectList(Long userId) {
    User findUser = userService.findById(userId); // 찾으려는 유저 불러오기
    List<StudySubject> studySubjects = subjectRepository.findAllByUser(findUser); // 유저의 목록을 가져온다.

    return studySubjects.stream()
        .map(studyResponseMapper::convertSubjectResponse)
        .toList();
  }

  @Transactional
  public StudySubject createSubject(CreateSubjectRequest dto) {
    User user = securityUtil.getCurrentUser();

    return subjectRepository.save(StudySubject.builder()
        .title(dto.getTitle())
        .user(user)
        .build());
  }

  @Transactional
  public void deleteSubject(Long subjectId) {
    User user = securityUtil.getCurrentUser();
    Optional<StudySubject> subject = subjectRepository.findById(subjectId);
    if (subject.isEmpty()) {
      log.warn("deleteSubject: Not Found Subject. userId={}, subjectId={}", user.getId(),
          subjectId);
      throw new NotFoundStudyException("과목을 찾을 수 없음");
    }

    if (!subject.get().getUser().equals(user)) { // 주인이 아닌 경우 예외 발생
      log.warn("deleteSubject: The users are not the same. userId={}, subjectId={}", user.getId(),
          subjectId);
      throw new PermissionException("유저가 같지 않음");
    }

    subjectRepository.delete(subject.get()); // 검증이 끝난 다음에 삭제
  }

  /**
   * 유저의 특정 월의 기록을 조회한다.
   *
   * @param findUser 검색할 유저
   * @param dto      검색할 월
   * @return List<StudyRecord>
   */
  @Transactional(readOnly = true)
  public List<StudyRecord> getUserRecodes(User findUser, GetUserRecodesRequest dto) {
    LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), FIRST_DAY_OF_MONTH);
    LocalDate endDate = DateTimeUtil.getLastDayOfMonth(dto.getYear(), dto.getMonth());

    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(END_OF_DAY);

    return studyRecordRepository.findByUserAndStartAtBetween(findUser, startDateTime, endDateTime);
  }
}
