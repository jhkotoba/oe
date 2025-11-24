package jkt.oe.application.authentication.login.port.in.dto;


public class LoginCommand {

    private final String loginId;
    private final String password;

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