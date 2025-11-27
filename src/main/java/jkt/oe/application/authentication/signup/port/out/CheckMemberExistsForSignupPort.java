package jkt.oe.application.authentication.signup.port.out;

import reactor.core.publisher.Mono;

public interface CheckMemberExistsForSignupPort {

    Mono<Boolean> existsMemberByLoginId(String loginId);
}
