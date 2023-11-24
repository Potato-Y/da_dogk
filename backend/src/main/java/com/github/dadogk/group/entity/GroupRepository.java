package com.github.dadogk.group.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByGroupNameContaining(String groupName);
}
