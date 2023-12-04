package com.github.dadogk.school.entity;

import com.github.dadogk.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolMemberRepostiory extends JpaRepository<SchoolMember, Long> {
    Optional<SchoolMember> findByMail(String mail);

    Optional<SchoolMember> findByUser(User user);
}
