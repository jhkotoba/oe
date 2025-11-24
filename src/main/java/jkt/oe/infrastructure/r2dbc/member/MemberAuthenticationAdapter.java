package jkt.oe.infrastructure.r2dbc.member;

import jkt.oe.application.authentication.login.port.out.LoadMemberForAuthenticationPort;
import jkt.oe.domain.authentication.AuthenticationMember;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MemberAuthenticationAdapter implements LoadMemberForAuthenticationPort{

    private final MemberRepository memberRepository;

    @Override
    public Mono<AuthenticationMember> loadMemberByLoginId(String loginId) {
       return memberRepository.findByLoginId(loginId)
                .map(this::mapToAuthenticationMember);
    }


    private AuthenticationMember mapToAuthenticationMember(MemberEntity entity) {

        // boolean enabled = "Y".equals(entity.getUseYn());
        // boolean locked = "Y".equals(entity.getLockedYn());
        // boolean withdrawn = "Y".equals(entity.getWithdrawnYn());
        
        return new AuthenticationMember(
                entity.getMemberId(),
                entity.getLoginId(),
                entity.getPassword(),
                entity.getSalt(),
                true,
                false,
                false
        );
    }
}
