package jkt.oe.module.auth.login.service;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import jkt.oe.config.exception.SystemException;
import jkt.oe.module.auth.login.exception.LoginException;
import jkt.oe.module.auth.login.model.data.UserData;
import jkt.oe.module.auth.login.model.request.LoginRequest;
import jkt.oe.module.auth.login.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
	
	/**
	 * 사용자가 입력한 비밀번호를 현재 저장된 해시와 비교하여 인증을 수행
	 * 
	 * @param request - 로그인 요청 정보(사용자 ID, 원문 비밀번호 등)
	 * @param user - DB에서 조회한 사용자 정보(저장된 해시, 솔트 등)
	 * @return
	 */
	public Mono<UserData> confirmUser(LoginRequest request, UserData user) {
		
		return Mono.fromCallable(() -> {
			
			// SHA-512 해시 생성
			MessageDigest md = MessageDigest.getInstance("SHA-512");
	        md.update(user.getSalt().getBytes(StandardCharsets.UTF_8));
	        md.update(request.getPassword().getBytes(StandardCharsets.UTF_8));
	        String encoded = String.format("%0128x", new BigInteger(1, md.digest()));	        
	        
	        // 타이밍 공격 방지를 위한 상수 시간 비교
	        byte[] actual = user.getPassword().getBytes(StandardCharsets.UTF_8);
	        byte[] expected = encoded.getBytes(StandardCharsets.UTF_8);
	        if (!MessageDigest.isEqual(actual, expected)) {
	            throw new LoginException(LoginException.Reason.INVALID_CREDENTIALS);
	        }	        
			
	        return user;
		})
		.subscribeOn(Schedulers.parallel())
		.onErrorMap(NoSuchAlgorithmException.class, ex -> {
			// SHA-512 알고리즘이 없을 때 예외처리
			return new SystemException(SystemException.Reason.NO_SUCH_ALGORITHM, ex);
		});
	}
}
