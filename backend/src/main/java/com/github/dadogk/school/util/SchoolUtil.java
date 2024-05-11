package com.github.dadogk.school.util;

import com.github.dadogk.school.dto.SchoolInfoResponse;
import com.github.dadogk.school.entity.SchoolMember;
import com.github.dadogk.study.StudyService;
import com.github.dadogk.study.dto.api.recode.GetUserRecodesRequest;
import com.github.dadogk.study.entity.StudyRecord;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchoolUtil {

  private final StudyService studyService;

  public SchoolInfoResponse convertSchoolInfo(SchoolMember mySchoolMember) {
    String schoolDomain = mySchoolMember.getSchool().getDomain();
    String schoolName = mySchoolMember.getSchool().getName();

    long averangeTime = 0L;
    int count = 0;

    List<SchoolMember> schoolMembers = mySchoolMember.getSchool().getSchoolMembers();
    for (SchoolMember schoolMember : schoolMembers) { // 그룹원 별로 계산
      List<StudyRecord> records = studyService.getUserRecodes(schoolMember.getUser(),
          new GetUserRecodesRequest());
      for (StudyRecord record : records) { // 공부 시간 초 단위로 계산
        Duration duration = Duration.between(record.getStartAt(), record.getEndAt());
        averangeTime += (int) duration.getSeconds();
      }
      count += records.size(); // 기록 수 추가

      if (count != 0) { // 0인 경우 나눌 수 없으니 그대로 반환한다.
        averangeTime /= count;
      }
    }

    return new SchoolInfoResponse(schoolDomain, schoolName, averangeTime);
  }
}