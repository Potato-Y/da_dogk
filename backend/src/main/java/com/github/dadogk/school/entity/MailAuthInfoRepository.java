package com.github.dadogk.school.entity;

import com.github.dadogk.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailAuthInfoRepository extends JpaRepository<MailAuthInfo, Long> {

  Optional<MailAuthInfo> findByUser(User user);
}
