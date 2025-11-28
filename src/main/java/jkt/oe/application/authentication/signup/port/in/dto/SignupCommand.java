package jkt.oe.application.authentication.signup.port.in.dto;

public class SignupCommand {

    private final String loginId;

    private final String memberName;

    private final String password;

    private final String email;

    public SignupCommand(String loginId, String memberName, String password, String email) {
        this.loginId = loginId;
        this.memberName = memberName;
        this.password = password;
        this.email = email;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
