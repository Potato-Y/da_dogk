package com.github.dadogk.study.entity;

import com.github.dadogk.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

  List<StudyRecord> findByUserAndStartAtBetween(User user, LocalDateTime start, LocalDateTime end);

  List<StudyRecord> findBySubjectAndStartAtBetween(StudySubject subject, LocalDateTime start,
      LocalDateTime end);
}
