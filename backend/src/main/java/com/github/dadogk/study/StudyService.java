package com.github.dadogk.study;

import com.github.dadogk.exceptions.PermissionException;
import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.study.dto.api.SubjectTitleResponse;
import com.github.dadogk.study.dto.api.create.CreateSubjectRequest;
import com.github.dadogk.study.dto.api.recode.GetUserRecodesRequest;
import com.github.dadogk.study.entity.StudyRecord;
import com.github.dadogk.study.entity.StudyRecordRepository;
import com.github.dadogk.study.entity.StudySubject;
import com.github.dadogk.study.entity.StudySubjectRepository;
import com.github.dadogk.study.exception.NotFoundStudyException;
import com.github.dadogk.user.util.UserUtil;
import com.github.dadogk.utils.DateTimeUtil;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Log4j2
public class StudyService {

    private static final Logger logger = LoggerFactory.getLogger(StudyService.class);
    private final StudySubjectRepository subjectRepository;
    private final StudyRecordRepository studyRecordRepository;
    private final SecurityUtil securityUtil;
    private final UserUtil userUtil;

    /**
     * 과목 기본 설정을 한다.
     *
     * @param user 유저 객체
     */
    public void defaultSetting(User user) {
        StudySubject subject = StudySubject.builder()
                .title("개인 공부")
                .user(user)
                .build();

        logger.info("defaultSetting. default 설정 추가 userId={}", user.getId());
        subjectRepository.save(subject);
    }

    public StudySubject getSubject(Long subjectId, Long userId) {
        Optional<StudySubject> subject = subjectRepository.findById(subjectId);
        if (subject.isEmpty()) {
            logger.warn("getSubject. 잘못된 StudySubject id={}", subjectId);
            throw new IllegalArgumentException(" 잘못된 StudySubject id");
        }

        validateSubjectUserMatch(subject.get(), userId);

        return subject.get();
    }

    private void validateSubjectUserMatch(StudySubject subject, Long userId) {
        if (subject.getUser().getId().equals(userId)) {
            return;
        }

        logger.warn("validateSubjectUserMatch. User와 Subject User가 동일하지 않음. userId={}, subjectId={}", userId,
                subject.getId());
        throw new IllegalArgumentException("User와 Subject user가 맞지 않음");
    }

    public StudyRecord startStudy(User user, StudySubject subject) {
        StudyRecord record = studyRecordRepository.save(StudyRecord.builder()
                .user(user)
                .subject(subject)
                .build());

        return record;
    }

    public StudyRecord endStudy(StudyRecord record) {
        return studyRecordRepository.save(record.updateEndAt());
    }

    /**
     * 특정 유저의 과목 리스트를 불러온다.
     *
     * @param userId 조회하려는 UserId
     * @return List<SubjectTitleResponse>
     */
    public List<SubjectTitleResponse> getUserStudySubjectList(Long userId) {
        User findUser = userUtil.findById(userId); // 찾으려는 유저 불러오기
        List<StudySubject> studySubjects = subjectRepository.findAllByUser(findUser); // 유저의 목록을 가져온다.

        List<SubjectTitleResponse> subjectTitleResponses = new ArrayList<>();
        if (studySubjects.isEmpty()) { // 만약 비어있다면 빈 리스트를 반환한다.
            return subjectTitleResponses;
        }

        UserResponse userResponse = userUtil.convertUserResponse(findUser);
        for (StudySubject subject : studySubjects) { // 유저의 과목 목록을 dto 리스트에 담는다.
            subjectTitleResponses.add(new SubjectTitleResponse(subject.getId(), userResponse, subject.getTitle()));
        }

        return subjectTitleResponses;
    }

    public StudySubject createSubject(CreateSubjectRequest dto) {
        User user = securityUtil.getCurrentUser();
        StudySubject subject = subjectRepository.save(StudySubject.builder()
                .title(dto.getTitle())
                .user(user)
                .build());

        log.info("createSubject: userId={}, subjectId={}", user.getId(), subject.getId());

        return subject;
    }

    public void deleteSubject(Long subjectId) {
        User user = securityUtil.getCurrentUser();
        Optional<StudySubject> subject = subjectRepository.findById(subjectId);
        if (subject.isEmpty()) {
            log.warn("deleteSubject: Not Found Subject. userId={}, subjectId={}", user.getId(), subjectId);
            throw new NotFoundStudyException("과목을 찾을 수 없음");
        }

        if (!subject.get().getUser().equals(user)) { // 주인이 아닌 경우 예외 발생
            log.warn("deleteSubject: The users are not the same. userId={}, subjectId={}", user.getId(), subjectId);
            throw new PermissionException("유저가 같지 않음");
        }

        subjectRepository.delete(subject.get()); // 검증이 끝난 다음에 삭제
    }

    /**
     * 특정 달의 기록 가져오기
     *
     * @param dto
     * @return List<StudyRecord>
     */
    public List<StudyRecord> getCurrentUserRecodes(GetUserRecodesRequest dto) {
        User user = securityUtil.getCurrentUser();
        LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
        LocalDate endDate = DateTimeUtil.getLastDayOfMonth(dto.getYear(), dto.getMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        List<StudyRecord> records = studyRecordRepository.findByUserAndStartAtBetween(user, startDateTime, endDateTime);

        return records;
    }

    /**
     * 유저의 특정 월의 기록을 조회한다.
     *
     * @param findUser 검색할 유저
     * @param dto 검색할 월
     * @return List<StudyRecord>
     */
    public List<StudyRecord> getUserRecodes(User findUser, GetUserRecodesRequest dto) {
        LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
        LocalDate endDate = DateTimeUtil.getLastDayOfMonth(dto.getYear(), dto.getMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<StudyRecord> records = studyRecordRepository.findByUserAndStartAtBetween(findUser, startDateTime,
                endDateTime);
        return records;
    }


    public Long getUserTodayStudyRecoders(User findUser) {
        LocalDate nowDate = LocalDate.now();
        LocalDateTime startTime = nowDate.atStartOfDay();
        LocalDateTime endTime = nowDate.atTime(23, 59, 59);

        List<StudyRecord> records = studyRecordRepository.findByUserAndStartAtBetween(findUser, startTime, endTime);
        Long todayStudyTime = 0L;
        for (StudyRecord record : records) {
            LocalDateTime startAt = record.getStartAt();
            LocalDateTime endAt = record.getEndAt();
            if (endAt == null) { // 만약 아직 공부가 끝나지 않았다면 현재 시간으로
                endAt = LocalDateTime.now();
            }

            todayStudyTime += Duration.between(startAt, endAt).getSeconds();
        }

        return todayStudyTime;
    }
}
