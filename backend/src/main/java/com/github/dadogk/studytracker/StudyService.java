package com.github.dadogk.studytracker;

import com.github.dadogk.studytracker.entity.StudySubject;
import com.github.dadogk.studytracker.entity.StudySubjectRepository;
import com.github.dadogk.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudyService {

    private static final Logger logger = LoggerFactory.getLogger(StudyService.class);
    private final StudySubjectRepository subjectRepository;

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
}
