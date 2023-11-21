package com.github.dadogk.studytracker;

import com.github.dadogk.studytracker.entity.StudyRecord;
import com.github.dadogk.studytracker.entity.StudyRecordRepository;
import com.github.dadogk.studytracker.entity.StudySubject;
import com.github.dadogk.studytracker.entity.StudySubjectRepository;
import com.github.dadogk.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudyService {

    private static final Logger logger = LoggerFactory.getLogger(StudyService.class);
    private final StudySubjectRepository subjectRepository;
    private final StudyRecordRepository studyRecordRepository;

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
}
