package jkt.oe.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jkt.oe.persistence.entity.UserEntity;
import reactor.core.publisher.Mono;

/**
 * ReactiveCrudRepository를 확장하여 UserEntity에 대한
 * 사용자 정보 조회 Repository 인터페이스
 */
@Repository
public interface UserEntityRepository extends ReactiveCrudRepository<UserEntity, Long> {
	
	/**
	 * 사용자 ID에 해당하는 UserEntity를 조회
	 * 
	 * @param userId - 클라이언트가 제공한 로그인 ID
	 * @return Mono<UserEntity> - 비동기적으로 반환되는 UserEntity 객체
	 */
	public Mono<UserEntity> findByLoginId(String loginId);
	
	/**
	 * 주어진 사용자 ID(userId)를 가진 사용자가 데이터베이스에 존재하는지 확인
	 * 
	 * @param userId - 클라이언트가 제공한 로그인 ID
	 * @return Mono<Boolean> 존재하면 true, 없으면 false 를 발행.
	 */
	public Mono<Boolean> existsByLoginId(String loginId);
}
