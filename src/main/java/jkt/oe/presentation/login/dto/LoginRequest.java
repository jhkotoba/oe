package jkt.oe.presentation.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 요청 DTO
 */
@Getter
@AllArgsConstructor
public class LoginRequest {
    
    // 로그인 아이디
    private String loginId;
    // 비밀번호
    private String password;
}
 