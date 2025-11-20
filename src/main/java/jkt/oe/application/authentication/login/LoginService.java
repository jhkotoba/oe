package jkt.oe.application.authentication.login;

import jkt.oe.application.authentication.login.port.in.LoginUseCase;
import jkt.oe.application.authentication.login.port.in.dto.LoginCommand;
import jkt.oe.application.authentication.login.port.in.dto.LoginResult;
import jkt.oe.application.authentication.token.port.in.IssueAccessTokenUseCase;
import reactor.core.publisher.Mono;

public class LoginService implements LoginUseCase {
    
    private final IssueAccessTokenUseCase issueAccessTokenUseCase;
    
    public LoginService(IssueAccessTokenUseCase issueAccessTokenUseCase) {
        this.issueAccessTokenUseCase = issueAccessTokenUseCase;
    }

    @Override
    public Mono<LoginResult> login(LoginCommand command) {
        return Mono.empty();
    }

}
