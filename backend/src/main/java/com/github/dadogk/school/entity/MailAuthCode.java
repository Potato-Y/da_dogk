package com.github.dadogk.school.entity;

import com.github.dadogk.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
    public MailAuthCode(User user,School school, String mail,String code ) {
        this.user = user;
        this.school = school;
        this.mail = mail;
        this.code = code;
    }

    public MailAuthCode updateNewAuth( School school, String mail,String code) {
        this.school = school;
        this.mail = mail;
        this.code = code;

        return this;
    }
}
