package jkt.oe.application.authentication.signup;

import java.time.LocalDateTime;

import jkt.oe.application.authentication.signup.port.in.SignupUseCase;
import jkt.oe.application.authentication.signup.port.in.dto.SignupCommand;
import jkt.oe.application.authentication.signup.port.in.dto.SignupResult;
import jkt.oe.application.authentication.signup.port.out.CheckMemberExistsForSignupPort;
import jkt.oe.domain.exception.LoginException;
import jkt.oe.domain.member.Member;
import reactor.core.publisher.Mono;

public class SignupService implements SignupUseCase {

    private final CheckMemberExistsForSignupPort checkMemberExistsForSignupPort;

    public SignupService(CheckMemberExistsForSignupPort checkMemberExistsForSignupPort) {
        this.checkMemberExistsForSignupPort = checkMemberExistsForSignupPort;
    }

    @Override
    public Mono<SignupResult> signup(SignupCommand command) {
        
        return checkMemberExistsForSignupPort.existsMemberByLoginId(command.getLoginId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new LoginException(LoginException.Reason.ALREADY_EXISTING_ACCOUNT));
                    } else {
                        

                        // 회원 가입 로직 구현 (예: 회원 정보 저장)
                        SignupResult result = new SignupResult();
                        return Mono.just(result);
                    }
                });


        
    }

}
