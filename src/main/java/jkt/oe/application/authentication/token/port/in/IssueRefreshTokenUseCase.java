package jkt.oe.application.authentication.token.port.in;

import reactor.core.publisher.Mono;

public interface IssueRefreshTokenUseCase {
    
    public Mono<String> issueRefreshToken();
}
