package jkt.oe.module.auth.token.refresh.service;

import java.util.List;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

import jkt.oe.config.security.JwtUtil;
import reactor.core.publisher.Mono;

@Service
public class RefreshTokenService {

	private final ReactiveRedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token 저장 (예: username을 키로 사용)
    public Mono<Boolean> saveRefreshToken(String username, String refreshToken, long expirationSeconds) {
        return redisTemplate.opsForValue().set(username, refreshToken)
                .then(redisTemplate.expire(username, java.time.Duration.ofSeconds(expirationSeconds)));
    }

    // 저장된 Refresh Token과 클라이언트에서 받은 Refresh Token 비교
    public Mono<Boolean> validateRefreshToken(String username, String refreshToken) {
        return redisTemplate.opsForValue().get(username)
                .map(storedToken -> storedToken.equals(refreshToken));
    }

    // Refresh Token이 유효하면 새 JWT 토큰을 생성하여 반환 (간단 예시)
    public Mono<String> validateAndRefreshToken(String username, String refreshToken) {
        return validateRefreshToken(username, refreshToken)
            .flatMap(valid -> {
                if (Boolean.TRUE.equals(valid)) {
                    // 예시: 새 JWT 토큰 생성 (만료시간 15분)
                    String newToken = JwtUtil.generateToken(username, List.of("ROLE_USER"), 15 * 60 * 1000);
                    return Mono.just(newToken);
                }
                return Mono.empty();
            });
    }
}
