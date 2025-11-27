package jkt.oe.application.authentication.token.port.in;

import reactor.core.publisher.Mono;

/**
 * 리프레시 토큰 발급 Use Case 인터페이스
 */
public interface IssueRefreshTokenUseCase {
    
    /**
     * 리프레시 토큰 발급
     * 
     * @return Mono<String> - 발급된 리프레시 토큰을 비동기적으로 반환
     */
    public Mono<String> issueRefreshToken();
}
