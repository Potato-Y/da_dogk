package com.github.dadogk.study.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.dadogk.user.entity.User;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    List<StudyRecord> findByUserAndStartAtBetween(User user, LocalDateTime start, LocalDateTime end);
}
