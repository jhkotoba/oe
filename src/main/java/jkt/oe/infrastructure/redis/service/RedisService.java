package jkt.oe.infrastructure.redis.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jkt.oe.infrastructure.redis.data.RefreshData;
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
	
    /**
     * HMAC 해시용 비밀키 버전(kid). 키 로테이션 시 교체.
     * 예) h-2025-11
     */
    //@Value("${custom.jwt.refresh-hmac.kid}")
    private String rtHmacKid = "h-2025-11"; // TODO test
    
    /**
     * HMAC 해시용 비밀키(바이트). 환경변수/시크릿매니저에서 32~64바이트 난수 권장.
     * 예) base64 인코딩된 값을 환경변수로 받고, 여기서 decode해서 byte[]로 주입하는 형태도 가능
     */
//    @Value("${refresh-hmac-secret-path}")
//    private String refreshHmacPath;
    
    private String refreshHmacSecret = "ASJDWIHGISDFJIE2AFAEDAGSDFVNEIEISLKJDLS"; // refreshHmacPath 임시
	
	private final ReactiveStringRedisTemplate redisTemplate;
	
	private final ObjectMapper objectMapper;
	
//	private final DefaultRedisScript<Long> MIGRATE_RT_SCRIPT =
//	    new DefaultRedisScript<>(
//	        """
//	        -- KEYS[1]=oldKey, KEYS[2]=newKey
//	        -- ARGV[1]=newJson, ARGV[2]=expectedOldJson(or ""), ARGV[3]=defaultTtlMs
//	        local v = redis.call('GET', KEYS[1])
//	        if (not v) then return 0 end
//	        if (ARGV[2] ~= '' and v ~= ARGV[2]) then return -1 end
//	        local ttl = redis.call('PTTL', KEYS[1])
//	        if (ttl < 0) then ttl = tonumber(ARGV[3]) end
//	        redis.call('SET', KEYS[2], ARGV[1], 'PX', ttl)
//	        redis.call('DEL', KEYS[1])
//	        return 1
//	        """,
//	        Long.class
//	    );
	
	
	public Mono<String> getRefreshToken(String uuid) {		
		return redisTemplate.opsForValue().get("uuid:" + uuid)
			.flatMap(userId -> redisTemplate.opsForValue().get("refresh:" + userId));
	}
	
    /**
     * 디바이스 식별자(IP + UserAgent)를 SHA-256 해시로 계산하고
     * 해당 키에 저장된 리프레시 토큰을 REDIS에서 조회합니다.
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
	
	@Deprecated
	public Mono<Boolean> storeUUIDByUserNo(String uuid, Long userNo){
		return redisTemplate.opsForValue()
                .set("uuid:" + uuid, String.valueOf(userNo), Duration.ofSeconds(this.refreshExpiration));
	}
	
//	@Deprecated
//	public Mono<Boolean> storeRefreshToken(Long userNo, String refreshToken){
//		return redisTemplate.opsForValue()
//                .set("refresh:" + userNo, refreshToken, Duration.ofSeconds(this.refreshExpiration));
//	}
	
	public Mono<Boolean> storeRefreshToken(Long userId, String refreshToken){
		
		Instant now = Instant.now();
        Instant exp = now.plusSeconds(this.refreshExpiration);
        
		
        // RT 해시 계산
        String rtHash = this.hmacSha256Base64Url(this.refreshHmacSecret.getBytes(), refreshToken);
        
        // Redis 키/값 구성
        String tag = "{u:" + userId + "}";
        String key = "rt:" + tag + ":" + rtHmacKid + ":" + rtHash;
        
        RefreshData entry = RefreshData.builder()
        	.userId(userId)
	        .issuedAt(now.toString())
	        .expiresAt(exp.toString())
	        .rotated(false)
	        .kid(rtHmacKid)
	        //.ua(ua) // TEST
	        //.ip(ip) // TEST
	        //.familyId(familyId != null ? familyId : UUID.randomUUID().toString()) // TEST 
	        .build();
        
        String json = null;
        try {
            json = objectMapper.writeValueAsString(entry);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
		
        Duration ttl = Duration.ofSeconds(this.refreshExpiration);
        return redisTemplate.opsForValue().set(key, json, ttl);
	}
	
	public Mono<Boolean> rotateRefreshToken(Long userId, String prevRefreshToken, String nextRefreshToken){
		
		Instant now = Instant.now();
        Instant exp = now.plusSeconds(this.refreshExpiration);
        
		
        // RT 해시 계산
        String rtHash = this.hmacSha256Base64Url(this.refreshHmacSecret.getBytes(), nextRefreshToken);
        
        // Redis 키/값 구성
        String tag = "{u:" + userId + "}";
        String key = "rt:" + tag + ":" + rtHmacKid + ":" + rtHash;
        
        RefreshData entry = RefreshData.builder()
        	.userId(userId)
	        .issuedAt(now.toString())
	        .expiresAt(exp.toString())
	        .rotated(false)
	        .kid(rtHmacKid)
	        //.ua(ua) // TEST
	        //.ip(ip) // TEST
	        //.familyId(familyId != null ? familyId : UUID.randomUUID().toString()) // TEST 
	        .build();
        
        String json = null;
        try {
            json = objectMapper.writeValueAsString(entry);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
		
        Duration ttl = Duration.ofSeconds(this.refreshExpiration);
        return redisTemplate.opsForValue().set(key, json, ttl);
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
	
	

	
	/**
     * RT 원문(rtRaw)에 대해 base64url(HMAC_SHA256(secret, rtRaw)) 값을 반환
     */
    private String hmacSha256Base64Url(byte[] secret, String rtRaw) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            byte[] out = mac.doFinal(rtRaw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 계산 실패", e);
        }
    }

}
