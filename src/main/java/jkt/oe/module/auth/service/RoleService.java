package jkt.oe.module.auth.service;
import org.springframework.stereotype.Service;

import jkt.oe.module.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 역활 관련 비지니스 로직을 수행하는 서비스
 */
@Service
@RequiredArgsConstructor
public class RoleService {
	
    private final RoleRepository roleRepository;

    /**
     * 신규 가입자에게 기본 ROLE_USER 를 부여
     * @param memberId 생성된 사용자 PK
     * @return Mono<Void>
     */
    public Mono<Void> insertSignupMemberRole(Long userId) {
    	return Mono.empty();
//        return roleRepository.insertUserRole(userId, "ROLE_USER");
    }
}