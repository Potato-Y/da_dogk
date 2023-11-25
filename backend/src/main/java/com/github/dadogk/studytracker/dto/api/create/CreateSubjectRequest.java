package com.github.dadogk.studytracker.dto.api.create;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateSubjectRequest {
    @NotBlank
    private String title;
}
