package jkt.oe.application.authentication.login.port.in.dto;

/**
 * 로그인 결과 DTO
 */
public class LoginResult {

    // 회원 식별자
    private final Long memberId;
    
    // 로그인 아이디
    private final String loginId;

    //  액세스 토큰
    private final String accessToken;
    
    //  리프레시 토큰
    private final String refreshToken;

    // 생성자
    public LoginResult(Long memberId, String loginId, String accessToken, String refreshToken) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;        
    }

    // 회원 식별자 조회
    public Long getMemberId() { 
        return memberId; 
    }

    // 로그인 아이디 조회
    public String getLoginId() { 
        return loginId; 
    }
    
    // 액세스 토큰 조회
    public String getAccessToken() {
        return accessToken;
    }

    // 리프레시 토큰 조회   
    public String getRefreshToken() {
        return refreshToken;
    }
}