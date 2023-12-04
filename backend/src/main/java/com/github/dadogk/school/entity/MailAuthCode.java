package com.github.dadogk.school.entity;

import com.github.dadogk.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class MailAuthCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "mail", nullable = false)
    private String mail;

    @ManyToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "school_domain", nullable = false)
    private School school;

    @Builder
    public MailAuthCode(String code, String mail, User user, School school) {
        this.code = code;
        this.mail = mail;
        this.user = user;
        this.school = school;
    }

    public MailAuthCode updateNewAuth(String code, School school,String mail) {
        this.code = code;
        this.school = school;
        this.mail=mail;

        return this;
    }
}