package jkt.oe.infrastructure.r2dbc.member;

import org.springframework.transaction.reactive.TransactionalOperator;

import jkt.oe.application.authentication.login.port.out.LoadMemberForAuthenticationPort;
import jkt.oe.application.authentication.signup.port.out.CheckMemberExistsForSignupPort;
import jkt.oe.application.authentication.signup.port.out.SaveMemberForSignupPort;
import jkt.oe.domain.authentication.AuthenticationMember;
import jkt.oe.domain.member.Member;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class MemberAdapter
        implements LoadMemberForAuthenticationPort, CheckMemberExistsForSignupPort, SaveMemberForSignupPort {

    private final MemberRepository memberRepository;
    private final MemberServiceRoleRepository memberServiceRoleRepository;
    private final RoleScopeRepository roleScopeRepository;
    private final AuthorizationRoleRepository authorizationRoleRepository;
    private final AuthorizationScopeRepository authorizationScopeRepository;

    private final TransactionalOperator transactionalOperator;

    /**
     * 로그인 ID를 기반으로 사용자 정보 조회
     */
    @Override
    public Mono<AuthenticationMember> loadMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .map(this::mapToAuthenticationMember);
    }

    @Override
    public Mono<Boolean> existsMemberByLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    @Override
    public Mono<Void> saveMember(Member member) {

        return transactionalOperator.execute(status -> memberRepository.save(mapToMemberEntity(member))
                .flatMap(savedEntity -> {
                    Member domainMember = mapToMemberDomain(savedEntity); // 임시
                    return Mono.just(domainMember);
                })
                .then()).single();

    }

    /**
     * MemberEntity를 AuthenticationMember로 매핑
     * 
     * @param entity - 회원 엔티티
     * @return AuthenticationMember - 인증용 회원 정보
     */
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
                false);
    }

    private Member mapToMemberDomain(MemberEntity entity) {
        return Member.reconstruct(null, null, null, null, null, false, null, null);
    }

    private MemberEntity mapToMemberEntity(Member member) {
        return MemberEntity.builder()
                .memberId(member.getMemberId())
                .loginId(member.getLoginId())
                .email(member.getEmail())
                .password(member.getPassword())
                .build();
    }

}
