package jkt.oe.infrastructure.redis;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jkt.oe.infrastructure.redis.data.RefreshData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Redis에 리프레시 토큰 메타데이터를 저장하는 저장소
 */
@Component
@RequiredArgsConstructor
public class StoreRepository {

    /**
     * RefreshToken의 유효 시간(초)
     */
    @Value("${custom.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private final ReactiveStringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    /**
     * HMAC 해시용 비밀키 버전(kid). 키 로테이션 시 교체.
     * 예) h-2025-11
     */
    // TODO TEST VALUE
    // @Value("${custom.jwt.refresh-hmac.kid}")
    private String rtHmacKid = "h-2025-11";

    // HMAC 해시용 비밀키(바이트). 임시
    private String refreshHmacSecret = "ASJDWIHGISDFJIE2AFAEDAGSDFVNEIEISLKJDLS";

    public Mono<Boolean> storeRefreshTokenMetadata(Long memberId, String refreshToken) {

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(this.refreshExpiration);

        // RT 해시 계산
        String rtHash = this.hmacSha256Base64Url(this.refreshHmacSecret.getBytes(), refreshToken);

        // Redis 키/값 구성
        String tag = "{u:" + memberId + "}";
        String key = "rt:" + tag + ":" + rtHmacKid + ":" + rtHash;

        RefreshData entry = RefreshData.builder()
                .memberId(memberId)
                .issuedAt(now.toString())
                .expiresAt(exp.toString())
                .rotated(false)
                .kid(rtHmacKid)
                // .ua(ua) // TEST
                // .ip(ip) // TEST
                // .familyId(familyId != null ? familyId : UUID.randomUUID().toString()) // TEST
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

    /**
     * HMAC-SHA256 해시를 Base64 URL 인코딩으로 반환
     * @param secret - 비밀키 바이트 배열
     * @param rtRaw - 원문 리프레시 토큰
     * @return 해시된 Base64 URL 문자열
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
//