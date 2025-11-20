package jkt.oe.module.auth.repository;

import jkt.oe.module.auth.model.data.MemberData;
import reactor.core.publisher.Mono;

/**
 * 로그인 기능과 관련된 데이터 조회를 위한 도메인 계층의 
 * 저장소 추상화 인터페이스
 */
@Deprecated
public interface LoginRepository {
	
	/**
	 * 로그인 ID에 해당하는 사용자 정보를 조회
	 * @param loginId - 클라이언트가 제공한 사용자 ID
	 * @return Mono<LoginResponse> - 비동기적으로 반환되는 사용자 정보
	 */
	public Mono<MemberData> findByLoginId(String loginId);
	
	public Mono<MemberData> findMember(Long memberId);
}
