package com.github.dadogk.studytracker;

import com.github.dadogk.security.util.SecurityUtil;
import com.github.dadogk.studytracker.dto.api.SubjectTitleResponse;
import com.github.dadogk.studytracker.dto.api.create.CreateSubjectRequest;
import com.github.dadogk.studytracker.entity.StudyRecord;
import com.github.dadogk.studytracker.entity.StudyRecordRepository;
import com.github.dadogk.studytracker.entity.StudySubject;
import com.github.dadogk.studytracker.entity.StudySubjectRepository;
import com.github.dadogk.studytracker.exception.NotFoundStudyException;
import com.github.dadogk.user.util.UserUtil;
import com.github.dadogk.user.dto.UserResponse;
import com.github.dadogk.user.entity.User;
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

    public StudyRecord startStudy(StudySubject subject) {
        StudyRecord record = studyRecordRepository.save(StudyRecord.builder()
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

        UserResponse userResponse = new UserResponse(findUser.getId(), findUser.getEmail(), findUser.getNickname());
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
            throw new RuntimeException("유저가 같지 않음");
        }

        subjectRepository.delete(subject.get()); // 검증이 끝난 다음에 삭제
    }
}
