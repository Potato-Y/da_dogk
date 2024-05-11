package com.github.dadogk.user.entity;

import com.github.dadogk.group.entity.Group;
import com.github.dadogk.group.entity.GroupMember;
import com.github.dadogk.school.entity.MailAuthInfo;
import com.github.dadogk.school.entity.SchoolMember;
import com.github.dadogk.study.entity.StudySubject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false)
  private Long id;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @OneToMany(mappedBy = "hostUser", cascade = CascadeType.REMOVE)
  private List<Group> groups = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  private List<GroupMember> groupMembers = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
  private List<StudySubject> studySubjects = new ArrayList<>();

  @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
  @JoinColumn(name = "school")
  private SchoolMember schoolMember;

  @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
  @JoinColumn(name = "mail_auth")
  private MailAuthInfo mailAuthInfo;

  @Builder
  public User(String email, String password, String nickname) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("user"));
  }

  @Override // 사용자 id 반환
  public String getUsername() {
    return email;
  }

  @Override // 사용자 패스워드 반환
  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    // 만료되었는지 확인하는 로직
    return true; // true -> 만료되지 않음
  }

  @Override
  public boolean isAccountNonLocked() {
    // 계정이 잠금되었는지 확인하는 로직
    return true; // true -> 잠금되지 않음
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // 패스워드가 만료되었는지 확인하는 로직
    return true; // true -> 만료되지 않음
  }

  // 계정 사용 가능 여부 반환

  @Override
  public boolean isEnabled() {
    // 계정이 사용 가능한지 확인하는 로직
    return true; // true -> 사용 가능
  }
}
