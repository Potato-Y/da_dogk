package com.github.dadogk.school.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, Long> {

  Optional<School> findByDomain(String domain);
}
