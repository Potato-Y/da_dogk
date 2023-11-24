package com.github.dadogk.group.entity;

import com.github.dadogk.user.entity.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    List<GroupMember> findAllByUser(User user);

    List<GroupMember> findAllByGroup(Group group);
}
