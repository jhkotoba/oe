package jkt.oe.application.authentication.login.port.in;

import jkt.oe.application.authentication.login.port.in.dto.LoginCommand;
import jkt.oe.application.authentication.login.port.in.dto.LoginResult;
import reactor.core.publisher.Mono;

/**
 * 인증 로그인 유스케이스 인터페이스
 */
public interface LoginUseCase {

    /**
     * 멤버 로그인
     *
     * @param command 로그인 커맨드
     * @return 로그인 결과
     */
    public Mono<LoginResult> login(LoginCommand command);
}
