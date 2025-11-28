package jkt.oe.application.authentication.signup;

import org.springframework.security.crypto.password.PasswordEncoder;

import jkt.oe.application.authentication.signup.port.in.SignupUseCase;
import jkt.oe.application.authentication.signup.port.in.dto.SignupCommand;
import jkt.oe.application.authentication.signup.port.in.dto.SignupResult;
import jkt.oe.application.authentication.signup.port.out.CheckMemberExistsForSignupPort;
import jkt.oe.application.authentication.signup.port.out.SaveMemberForSignupPort;
import jkt.oe.domain.exception.LoginException;
import jkt.oe.domain.member.Member;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class SignupService implements SignupUseCase {

    private final CheckMemberExistsForSignupPort checkMemberExistsForSignupPort;

    private final SaveMemberForSignupPort saveMemberForSignupPort;

    private final PasswordEncoder passwordEncoder;

    public SignupService(CheckMemberExistsForSignupPort checkMemberExistsForSignupPort,
            SaveMemberForSignupPort saveMemberForSignupPort,
            PasswordEncoder passwordEncoder) {
        this.checkMemberExistsForSignupPort = checkMemberExistsForSignupPort;
        this.saveMemberForSignupPort = saveMemberForSignupPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<SignupResult> signup(SignupCommand command) {

        return checkMemberExistsForSignupPort.existsMemberByLoginId(command.getLoginId())
                .flatMap(exists -> {
                    if (exists) {
                        // 이미 존재하는 회원인 경우 예외 처리
                        return Mono.error(new LoginException(LoginException.Reason.ALREADY_EXISTING_ACCOUNT));
                    } else {
                        // 신규 회원 생성
                        return createMemberForSignup(command);
                    }
                })
                // 저장 후 결과 반환
                .flatMap(member -> saveMemberForSignupPort.saveMember(member)
                        .thenReturn(buildResult(member)));

    }

    private Mono<Member> createMemberForSignup(SignupCommand command) {

        return Mono.fromCallable(() -> {
            // Argon2id 인코딩
            String encodedPassword = passwordEncoder.encode(command.getPassword());

            return Member.createForSignup(
                    command.getLoginId(),
                    command.getEmail(),
                    encodedPassword);
        })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private SignupResult buildResult(Member member) {
        SignupResult result = new SignupResult();
        return result;
    }

}
