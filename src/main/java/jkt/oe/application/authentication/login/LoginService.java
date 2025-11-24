package jkt.oe.application.authentication.login;

import jkt.oe.application.authentication.login.port.in.LoginUseCase;
import jkt.oe.application.authentication.login.port.in.dto.LoginCommand;
import jkt.oe.application.authentication.login.port.in.dto.LoginResult;
import jkt.oe.application.authentication.login.port.out.LoadMemberForAuthenticationPort;
import jkt.oe.application.authentication.token.port.in.IssueAccessTokenUseCase;
import jkt.oe.application.authentication.token.port.in.IssueRefreshTokenUseCase;
import jkt.oe.domain.authentication.AuthenticationMember;
import jkt.oe.domain.authentication.policy.PasswordHashPolicy;
import jkt.oe.domain.exception.LoginException;
import reactor.core.publisher.Mono;

/**
 * 로그인 서비스
 */
public class LoginService implements LoginUseCase {

    /**
     * 액세스 토큰 발급 유스케이스
     */
    private final IssueAccessTokenUseCase issueAccessTokenUseCase;

    /**
     * 리프레시 토큰 발급 유스케이스
     */
    private final IssueRefreshTokenUseCase issueRefreshTokenUseCase;

    /**
     * 인증을 위한 회원 정보를 조회하는 포트
     */
    private final LoadMemberForAuthenticationPort loadMemberForAuthenticationPort;

    /**
     * 비밀번호 해시 정책
     */
    private final PasswordHashPolicy passwordHashPolicy;

    /**
     * 로그인 생성자
     * 
     * @param issueAccessTokenUseCase         - 액세스 토큰 발급 유스케이스
     * @param issueRefreshTokenUseCase        - 리프레시 토큰 발급 유스케이스
     * @param loadMemberForAuthenticationPort - 인증을 위한 회원 정보를 조회하는 포트
     * @param passwordHashPolicy              - 비밀번호 해시 정책
     */
    public LoginService(IssueAccessTokenUseCase issueAccessTokenUseCase,
            IssueRefreshTokenUseCase issueRefreshTokenUseCase,
            LoadMemberForAuthenticationPort loadMemberForAuthenticationPort,
            PasswordHashPolicy passwordHashPolicy) {
        this.issueAccessTokenUseCase = issueAccessTokenUseCase;
        this.issueRefreshTokenUseCase = issueRefreshTokenUseCase;
        this.loadMemberForAuthenticationPort = loadMemberForAuthenticationPort;
        this.passwordHashPolicy = passwordHashPolicy;
    }

    /**
     * 멤버 로그인
     */
    @Override
    public Mono<LoginResult> login(LoginCommand command) {

        // 1. 로그인 아이디로 사용자 조회
        return loadMemberForAuthenticationPort.loadMemberByLoginId(command.getLoginId())
                .switchIfEmpty(Mono.error(new LoginException(LoginException.Reason.MEMBER_NOT_FOUND)))
                // 2. 비밀번호 검증
                .flatMap(authenticationMember -> this.validatePassword(command, authenticationMember))
                // 3. 토큰 발급
                .flatMap(authenticationMember -> Mono.zip(
                        // Access 토큰 생성 - tuple1
                        issueAccessTokenUseCase.issueAccessToken(),
                        // Refresh 토큰 생성 - tuple2
                        issueRefreshTokenUseCase.issueRefreshToken(),
                        // 사용자 정보 - tuple3
                        Mono.just(authenticationMember))

                ).flatMap(tuple -> {
                    String accessToken = tuple.getT1();
                    String refreshToken = tuple.getT2();
                    // AuthenticationMember member = tuple.getT3();
                    return Mono.just(new LoginResult());
                });
    }

    /**
     * 비밀번호 검증
     * @param command - 로그인 커맨드 객체
     * @param member - 인증된 회원 정보
     * @return Mono<AuthenticationMember> - 검증된 회원 정보
     */
    private Mono<AuthenticationMember> validatePassword(LoginCommand command, AuthenticationMember member) {

        boolean matches = passwordHashPolicy.matches(
                command.getPassword(),
                member.getSalt(),
                member.getPassword());

        if (!matches) {
            return Mono.error(new LoginException(LoginException.Reason.INVALID_CREDENTIALS));
        }

        // // 계정 상태 체크도 여기서 같이
        // if (!member.canLogin()) {
        // return Mono.error(new LoginException(LoginException.Reason.LOCKED_ACCOUNT));
        // }

        return Mono.just(member);
    }
}
