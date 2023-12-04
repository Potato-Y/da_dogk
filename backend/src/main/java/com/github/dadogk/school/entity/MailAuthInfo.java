package com.github.dadogk.school.entity;

import com.github.dadogk.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mail_auth_info")
@Getter
@Entity
public class MailAuthInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_domain", nullable = false)
    private School school;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "mail", nullable = false)
    private String mail;

    @Builder
    public MailAuthInfo(User user, School school, String mail, String code ) {
        this.user = user;
        this.school = school;
        this.mail = mail;
        this.code = code;
    }

    public MailAuthInfo updateNewAuth(School school, String mail, String code) {
        this.school = school;
        this.mail = mail;
        this.code = code;

        return this;
    }
}
