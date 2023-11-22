package com.github.dadogk.user;

import com.github.dadogk.studytracker.StudyService;
import com.github.dadogk.user.dto.AddUserRequest;
import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final StudyService studyService;

    public User save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .build());

        logger.info("save. userId={}, userEmail={}, userNickname={}",
                user.getId(), user.getEmail(), user.getNickname());

        studyService.defaultSetting(user); // 과목 기본 설정
        return user;
    }
}
