package jkt.oe.module.auth.signup.repository;

import reactor.core.publisher.Mono;


public interface SignupRepository {
	
	/**
	 * 주어진 사용자 ID(userId)를 가진 사용자가 데이터베이스에 존재하는지 확인
	 * 
     * @param userId 조회할 사용자 ID
     * @return 사용자가 존재하면 Mono<Boolean>이 true, 그렇지 않으면 false 를 발행
	 */
	public Mono<Boolean> existsByUserId(String userId);
}
