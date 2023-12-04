package com.github.dadogk.school.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "schools")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "doamin", nullable = false)
    private String domain;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "school")
    private List<SchoolMember> schoolMembers = new ArrayList<>();
}
