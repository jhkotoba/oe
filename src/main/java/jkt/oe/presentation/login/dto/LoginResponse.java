package jkt.oe.presentation.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    // 회원 식별자
    private final Long memberId;
    // 로그인 아이디
    private final String loginId;
    // 액세스 토큰
    private final String accessToken;
    // 리프레시 토큰
    private final String refreshToken;
    
}
