package com.github.dadogk.school.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolMemberRepostiory extends JpaRepository<SchoolMember, Long> {
    Optional<SchoolMember> findByMail(String mail);
}
