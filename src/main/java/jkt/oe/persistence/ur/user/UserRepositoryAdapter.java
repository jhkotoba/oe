package jkt.oe.persistence.ur.user;

import org.springframework.stereotype.Repository;

import jkt.oe.module.auth.login.model.data.UserData;
import jkt.oe.module.auth.login.repository.LoginRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 도메인 계층의 LoginRepository 인터페이스를 구현하는 어댑터 클래스
 */
@Repository
@AllArgsConstructor
public class UserRepositoryAdapter implements LoginRepository {
	
	/**
	 * 사용자 정보 조회 Entity Repository 인터페이스
	 */
	private final UserEntityRepository userEntityRepository;
	
	/**
	 * 응답객체 변환
	 */
	private final UserMapper userMapper;
	
	/**
	 * 사용자 ID를 이용해 DB에서 UserEntity를 조회, LoginResponse로 변환하여 반환
	 */
	@Override
	public Mono<UserData> findByUserId(String userId) {
		return userEntityRepository.findByUserId(userId)
			.map(userMapper::findUser);
	}
}
