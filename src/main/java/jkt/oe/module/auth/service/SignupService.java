package jkt.oe.module.auth.service;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.stereotype.Service;

import jkt.oe.config.exception.SystemException;
import jkt.oe.module.auth.exception.SignupException;
import jkt.oe.module.auth.model.data.MemberData;
import jkt.oe.module.auth.model.request.SignupRequest;
import jkt.oe.module.auth.repository.SignupRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 가입 관련 비즈니스 로직을 수행하는 서비스
 */
@Service
@RequiredArgsConstructor
@Deprecated
public class SignupService {
	
	private final SignupRepository signupRepository;
	
	/**
	 * 
	 * @param loginId
	 * @return
	 */
	public Mono<Void> existsByLoginId(String loginId) {		
		return signupRepository.existsByLoginId(loginId)
			.flatMap(exists -> exists
                ? Mono.error(new SignupException(SignupException.Reason.LOGIN_ID_ALREADY_EXISTS))
                : Mono.empty()
            );
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public Mono<MemberData> saveMember(SignupRequest request) {
		
        return Mono.fromCallable(() -> {
            // 랜덤 salt 생성
            SecureRandom sr = new SecureRandom();
            byte[] saltBytes = new byte[16];
            sr.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);

            // SHA-512 해시 계산 (salt + password)
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            md.update(request.getPassword().getBytes(StandardCharsets.UTF_8));
            String hashed = String.format("%0128x", new BigInteger(1, md.digest()));

            // memberData 빌드 (salt, 해시된 password 세팅)
            return MemberData.builder()
                .loginId(request.getLoginId())
                .email(request.getEmail())
                .password(hashed)
                .salt(salt)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        })
        // CPU-bound 작업이니 parallel 스케줄러 권장
        .subscribeOn(Schedulers.parallel())
        // 빌드한 memberData 로 DB 저장
        .flatMap(memberData -> signupRepository.saveMember(memberData))
        // SHA-512 알고리즘 미지원 시 시스템 예외로 매핑
        .onErrorMap(NoSuchAlgorithmException.class,
            ex -> new SystemException(SystemException.Reason.NO_SUCH_ALGORITHM, ex)
        );
    }
}