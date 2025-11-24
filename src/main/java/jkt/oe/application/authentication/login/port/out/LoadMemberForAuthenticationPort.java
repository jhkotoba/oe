package jkt.oe.application.authentication.login.port.out;

import jkt.oe.domain.authentication.AuthenticationMember;
import reactor.core.publisher.Mono;

public interface LoadMemberForAuthenticationPort {

    public Mono<AuthenticationMember> loadMemberByLoginId(String loginId);
}
