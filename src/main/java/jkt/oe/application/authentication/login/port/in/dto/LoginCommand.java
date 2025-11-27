package jkt.oe.application.authentication.login.port.in.dto;

/**
 * 로그인 커맨드 DTO
 */
public class LoginCommand {

    // 로그인 아이디
    private final String loginId;
    // 비밀번호 
    private final String password;

    /**
     * 로그인 커맨드 생성자
     * @param loginId 로그인 아이디
     * @param password 비밀번호
     */
    public LoginCommand(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
    
    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }
}