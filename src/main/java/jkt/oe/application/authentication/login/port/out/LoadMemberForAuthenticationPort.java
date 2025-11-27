package jkt.oe.application.authentication.login.port.out;

import jkt.oe.domain.authentication.AuthenticationMember;
import reactor.core.publisher.Mono;

/**
 * 회원 인증을 위한 사용자 정보를 조회하는 포트 인터페이스
 */
public interface LoadMemberForAuthenticationPort {

    /**
     * 로그인 ID를 기반으로 사용자 정보 조회
     * 
     * @param loginId - 로그인 아이디
     * @return Mono<AuthenticationMember> - 인증용 회원 정보를 비동기적으로 반환
     */ 
    public Mono<AuthenticationMember> loadMemberByLoginId(String loginId);
}
