package com.github.dadogk.group.entity;

import com.github.dadogk.enums.State;
import com.github.dadogk.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id; // 자동 생성 고유 ID

    @Column(name = "group_uri", nullable = false, unique = true, updatable = false)
    private String groupUri; // 외부에 사용될 고유 id

    @Column(name = "group_name", nullable = false)
    private String groupName; // 그룹 이름

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User hostUser; // 방장 user id

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private GroupType type;

    @Column(name = "privacy_state", nullable = false)
    private boolean privacyState; // true: 비공개, false: 공개

    @Column(name = "password")
    private String password;

    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Group(String groupUri, String groupName, User hostUser, State state, GroupType type, boolean privacyState,
                 String password) {
        this.groupUri = groupUri;
        this.groupName = groupName;
        this.hostUser = hostUser;
        this.state = state;
        this.type = type;
        this.privacyState = privacyState;
        this.password = password;
    }

    /**
     * group state 변경
     *
     * @param state
     * @return
     */
    public Group updateState(State state) {
        this.state = state;

        return this;
    }

    public Group updatePrivacyState(boolean privacyState) {
        this.privacyState = privacyState;

        return this;
    }
}
