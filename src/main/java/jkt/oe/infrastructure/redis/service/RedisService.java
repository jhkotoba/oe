package jkt.oe.infrastructure.redis.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import jkt.oe.infrastructure.redis.data.StoreRefreshTokenData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class RedisService {
	
	/**
	 * RefreshToken의 유효 시간(초)
	 */
	@Value("${custom.jwt.refresh-expiration}")
	private Long refreshExpiration;
	
	private final ReactiveStringRedisTemplate redisTemplate;
	
	
	public Mono<String> getRefreshToken(String uuid) {		
		return redisTemplate.opsForValue().get("uuid:" + uuid)
			.flatMap(userNo -> redisTemplate.opsForValue().get("refresh:" + userNo));
	}
	
    /**
     * 디바이스 식별자(IP + UserAgent)를 SHA-256 해시로 계산하고
     * 해당 키에 저장된 리프레시 토큰을 Redis에서 조회합니다.
     *
     * @param storeData 사용자 정보(회원번호, IP, UserAgent)
     * @return Mono<String> - 조회된 리프레시 토큰. 없으면 empty.
     */
	@Deprecated
    public Mono<String> getRefreshToken(StoreRefreshTokenData storeData) {	
        // IP + UserAgent 조합
        String input = storeData.getIp() + ":" + storeData.getUserAgent();
        
        return Mono.fromCallable(() -> {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                return hexString.toString();
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(deviceHash -> {
                String redisKey = "refresh:" + storeData.getUserNo() + ":" + deviceHash;            	
                return redisTemplate.opsForValue().get(redisKey);
            });
    }
	
	
	public Mono<Boolean> storeUUIDByUserNo(String uuid, Long userNo){
		return redisTemplate.opsForValue()
                .set("uuid:" + uuid, String.valueOf(userNo), Duration.ofSeconds(this.refreshExpiration));
	}
	
	public Mono<Boolean> storeRefreshToken(Long userNo, String refreshToken){
		return redisTemplate.opsForValue()
                .set("refresh:" + userNo, refreshToken, Duration.ofSeconds(this.refreshExpiration));
	}
	
	
	
	@Deprecated
	public Mono<Void> storeRefreshToken(StoreRefreshTokenData storeData){
		
		String input = storeData.getIp() + ":" + storeData.getUserAgent();
		
		return Mono.fromCallable(() -> {
            // SHA-256 해싱
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        })
        // 해싱은 블로킹 작업이 될 수 있으므로 boundedElastic 스케줄러 사용
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(deviceHash -> {
            String redisKey = "refresh:" + storeData.getUserNo() + ":" + deviceHash;
            Duration ttl = Duration.ofSeconds(this.refreshExpiration);
            // Redis에 저장
            return redisTemplate.opsForValue()
                    .set(redisKey, storeData.getRefreshToken(), ttl);
        })
        .then();
	}
	
	public Mono<Boolean> removeUUID(String uuid){
		return redisTemplate.delete(uuid).map(deletedCount -> deletedCount > 0);
		
		
	}

}
