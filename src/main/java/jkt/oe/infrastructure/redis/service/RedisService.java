package jkt.oe.infrastructure.redis.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import jkt.oe.infrastructure.redis.data.StoreRefreshTokenData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedisService {
	
	/**
	 * RefreshToken의 유효 시간(초)
	 */
	@Value("${custom.jwt.refresh-expiration}")
	private Long refreshExpiration;
	
	private final ReactiveStringRedisTemplate redisTemplate;
	
	public Mono<Void> storeRefreshToken(StoreRefreshTokenData storeData){
		
		String input = storeData.getIp() + ":" + storeData.getUserAgent();
		String deviceHash = null;
		
		try {
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
            deviceHash = hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			return Mono.error(e);
		}
		
		String redisKey = "refresh:" + storeData.getUserNo() + ":" + deviceHash;
		
		Duration ttl = Duration.ofSeconds(this.refreshExpiration);
		
		return redisTemplate.opsForValue()
		        .set(redisKey, storeData.getRefreshToken(), ttl)
		        .then();
	}

}
