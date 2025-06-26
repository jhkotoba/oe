package jkt.oe.persistence.ur.user;

import org.springframework.stereotype.Repository;

import jkt.oe.module.auth.model.data.UserData;
import jkt.oe.module.auth.repository.LoginRepository;
import jkt.oe.module.auth.repository.SignupRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 도메인 계층의 사용자 관련 Repository 인터페이스를 구현하는 어댑터 클래스
 */
@Repository
@AllArgsConstructor
public class UserRepositoryAdapter implements LoginRepository, SignupRepository {
	
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
	 * 
	 * @param userId 조회할 사용자 ID
	 * @return 조회된 UserEntity를 UserData로 매핑한 Mono를 반환하며, 해당 사용자가 없으면 빈 Mono를 반환
	 */
	@Override
	public Mono<UserData> findByUserId(String userId) {
		return userEntityRepository.findByUserId(userId)
			.map(userMapper::findUser);
	}
	
	/**
	 * 주어진 사용자 ID(userId)를 가진 사용자가 데이터베이스에 존재하는지 확인
	 * 
     * @param userId 조회할 사용자 ID
     * @return 사용자가 존재하면 Mono<Boolean>이 true, 그렇지 않으면 false 를 발행
	 */
	@Override
	public Mono<Boolean> existsByUserId(String userId) {
		return userEntityRepository.existsByUserId(userId);
	}
	
	/**
	 * 
	 */
	@Override
	public Mono<Void> saveUser(UserData user) {
		return userEntityRepository
			    .save(userMapper.saveUser(user))
			    .then();
	}
}
