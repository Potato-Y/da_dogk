package com.github.dadogk.user;

import com.github.dadogk.user.entity.User;
import com.github.dadogk.user.entity.UserRepository;
import com.github.dadogk.user.exception.NotFoundUserException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;

    /**
     * @param userId User db id
     * @return User
     */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException("Unexpected user"));
    }
}
