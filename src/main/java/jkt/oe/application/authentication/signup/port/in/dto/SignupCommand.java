package jkt.oe.application.authentication.signup.port.in.dto;

public class SignupCommand {

    private String loginId;

    private String memberName;

    public SignupCommand(String loginId, String memberName) {
        this.loginId = loginId;
        this.memberName = memberName;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getMemberName() {
        return memberName;
    }
}
