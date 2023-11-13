package com.github.dadogk.studytracker.entity;

import com.github.dadogk.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "study_subjects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class StudySubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id; // 자동 생성 고유 ID

    @ManyToOne
    @Column(name = "user_id", updatable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Builder
    public StudySubject(User user, String title) {
        this.user = user;
        this.title = title;
    }

    public StudySubject updateTitle(String title) {
        this.title = title;

        return this;
    }
}
