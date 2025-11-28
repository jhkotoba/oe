// package jkt.oe.infrastructure.r2dbc.member;

// import jkt.oe.application.authentication.login.port.out.LoadMemberForAuthenticationPort;
// import jkt.oe.domain.authentication.AuthenticationMember;
// import lombok.RequiredArgsConstructor;
// import reactor.core.publisher.Mono;

// /**
//  * 회원 인증을 위한 사용자 정보를 조회하는 어댑터 구현체
//  */
// @RequiredArgsConstructor
// public class MemberAuthenticationAdapter implements LoadMemberForAuthenticationPort{

//     /**
//      * 회원 리포지토리
//      */
//     private final MemberRepository memberRepository;

//     /** 
//      * 로그인 ID를 기반으로 사용자 정보 조회
//      */
//     @Override
//     public Mono<AuthenticationMember> loadMemberByLoginId(String loginId) {
//        return memberRepository.findByLoginId(loginId)
//                 .map(this::mapToAuthenticationMember);
//     }

//     /**
//      * MemberEntity를 AuthenticationMember로 매핑
//      * @param entity - 회원 엔티티
//      * @return AuthenticationMember - 인증용 회원 정보
//      */
//     private AuthenticationMember mapToAuthenticationMember(MemberEntity entity) {

//         // boolean enabled = "Y".equals(entity.getUseYn());
//         // boolean locked = "Y".equals(entity.getLockedYn());
//         // boolean withdrawn = "Y".equals(entity.getWithdrawnYn());
        
//         return new AuthenticationMember(
//                 entity.getMemberId(),
//                 entity.getLoginId(),
//                 entity.getPassword(),
//                 entity.getSalt(),
//                 true,
//                 false,
//                 false
//         );
//     }
// }
