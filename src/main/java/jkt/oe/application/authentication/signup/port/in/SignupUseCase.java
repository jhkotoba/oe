package jkt.oe.application.authentication.signup.port.in;

import jkt.oe.application.authentication.signup.port.in.dto.SignupCommand;
import jkt.oe.application.authentication.signup.port.in.dto.SignupResult;
import reactor.core.publisher.Mono;

public interface SignupUseCase {

    public Mono<SignupResult> signup(SignupCommand command);

}
