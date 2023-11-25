package com.github.dadogk.study.entity;

import com.github.dadogk.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE)
    private List<StudyRecord> records = new ArrayList<>();

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
