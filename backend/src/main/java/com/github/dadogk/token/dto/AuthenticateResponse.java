package com.github.dadogk.token.dto;

import com.github.dadogk.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticateResponse {
    private String accessToken;
    private String refreshToken;

    private UserResponse user;
}
