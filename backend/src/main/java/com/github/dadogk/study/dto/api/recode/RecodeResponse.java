package com.github.dadogk.study.dto.api.recode;

import com.github.dadogk.study.dto.api.SubjectResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RecodeResponse {

  private SubjectResponse subject;
  private LocalDateTime startAt;
  private LocalDateTime endAt;
}
