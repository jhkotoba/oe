package jkt.oe.persistence.ur.user;

import org.springframework.stereotype.Repository;

import jkt.oe.module.auth.login.model.response.LoginResponse;
import jkt.oe.module.auth.login.repository.LoginRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class UserRepositoryAdapter implements LoginRepository {
	
	private final UserEntityRepository userEntityRepository;
	private final UserMapper userMapper;
	
	@Override
	public Mono<LoginResponse> findByUserId(String userId) {
		return userEntityRepository.findByUserId(userId)
			.map(userMapper::findUser);
			
	}

	

	

}
