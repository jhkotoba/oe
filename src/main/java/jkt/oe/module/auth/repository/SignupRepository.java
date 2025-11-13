package jkt.oe.module.auth.repository;

import jkt.oe.module.auth.model.data.MemberData;
import reactor.core.publisher.Mono;


public interface SignupRepository {
	
	/**
	 * 주어진 로그인 ID(loginId)를 가진 사용자가 데이터베이스에 존재하는지 확인
	 * 
     * @param loginId 조회할 로그인 ID
     * @return 사용자가 존재하면 Mono<Boolean>이 true, 그렇지 않으면 false 를 발행
	 */
	public Mono<Boolean> existsByLoginId(String loginId);
	
	/**
     * 사용자 저장
     * @param member 저장할 사용자 데이터
     * @return 생성된 사용자 PK(MEMBER_ID)
     */
	public Mono<MemberData> saveMember(MemberData member);
}
