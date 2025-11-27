package jkt.oe.presentation.login;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import jkt.oe.application.authentication.login.port.in.LoginUseCase;
import jkt.oe.presentation.common.CookieFactory;
import jkt.oe.presentation.login.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 로그인 요청을 처리하는 핸들러 객체
 */
@Component
@RequiredArgsConstructor
public class LoginHandler {

    /**
     * 로그인 관련 비즈니스 로직을 수행하는 유스케이스
     */
    private final LoginUseCase loginUseCase;

    /**
     * 로그인 관련 응답 객체 변환 Mapper
     */
    private final LoginMapper loginMapper;

    /**
     * 쿠키 생성 팩토리
     */
    private final CookieFactory cookieFactory;

    /**
     * 로그인 요청 처리
     * @param serverRequest - 서버 요청 객체
     * @return 서버 응답 객체
     */
    public Mono<ServerResponse> loginProcess(ServerRequest serverRequest) {

        // 요청 바디를 LoginRequest 객체로 변환 후 처리
        return serverRequest.bodyToMono(LoginRequest.class)
                // LoginRequest 를 LoginCommand 로 변환
                .map(loginMapper::toCommand)
                // 로그인 유스케이스 호출
                .flatMap(loginUseCase::login)
                // LoginResult 를 LoginResponse 로 변환 및 쿠키 생성
                .flatMap(loginResult -> Mono.zip(
                        Mono.just(loginResult),
                        cookieFactory.createAccessTokenCookie(loginResult.getAccessToken()),
                        cookieFactory.createRefreshTokenCookie(loginResult.getRefreshToken())))
                // 서버 응답 생성
                .flatMap(tuple -> {
                    return ServerResponse.ok()
                            .cookie(tuple.getT2())
                            .cookie(tuple.getT3())
                            .bodyValue(loginMapper.toResponse(tuple.getT1()));
                });

    }
}
