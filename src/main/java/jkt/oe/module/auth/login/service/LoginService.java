package jkt.oe.module.auth.login.service;
import org.springframework.stereotype.Service;

import jkt.oe.module.auth.login.exception.LoginException;
import jkt.oe.module.auth.login.model.data.UserData;
import jkt.oe.module.auth.login.model.request.LoginRequest;
import jkt.oe.module.auth.login.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 로그인 관련 비즈니스 로직을 수행하는 서비스
 */
@Service
@RequiredArgsConstructor
public class LoginService {
	
	/**
	 * 로그인 관련 비즈니스 로직을 수행하기 위한 repository
	 */
	private final LoginRepository loginRepository;
	
	/**
	 * 사용자 ID를 기반으로 사용자 정보 조회
	 * @param LoginRequest - 클라이언트가 요청 데이터
	 * @return Mono<LoginResponse> - 비동기적으로 반환되는 사용자 정보
	 */
	public Mono<UserData> findUser(LoginRequest request){
		
		return loginRepository.findByUserId(request.getUserId())
                .switchIfEmpty(Mono.error(new LoginException(LoginException.Reason.USER_NOT_FOUND)));
	}
	
	public Mono<UserData> confirmUser(LoginRequest request, UserData user) {
		
		return Mono.just(user);
	}

}
