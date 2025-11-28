package jkt.oe.application.authentication.signup.port.out;

import jkt.oe.domain.member.Member;
import reactor.core.publisher.Mono;

public interface SaveMemberForSignupPort {
    
    Mono<Void> saveMember(Member member);
}
