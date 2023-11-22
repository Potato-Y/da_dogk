package com.github.dadogk.studytracker.entity;

import com.github.dadogk.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudySubjectRepository extends JpaRepository<StudySubject, Long> {
    List<StudySubject> findAllByUser(User user);
}
