package jkt.oe.presentation.login;

import jkt.oe.application.authentication.login.port.in.dto.LoginCommand;
import jkt.oe.presentation.login.dto.LoginRequest;

public class LoginMapper {

    public LoginCommand toCommand(LoginRequest r) {
        return new LoginCommand();
    }
}
