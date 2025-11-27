package jkt.oe.infrastructure.redis;

import org.springframework.stereotype.Component;

import jkt.oe.application.authentication.token.port.out.StoreRefreshTokenMetadataPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Redis에 리프레시 토큰 메타데이터를 저장하는 어댑터 구현체
 */
@Component
@RequiredArgsConstructor
public class StoreAdapter implements StoreRefreshTokenMetadataPort {

    /**
     * 리프레시 토큰 메타데이터 저장소
     */
    private final StoreRepository storeRepository;

    /**
     * 리프레시 토큰 메타데이터를 Redis에 저장
     * 
     * @param memberId     회원 식별자
     * @param refreshToken 리프레시 토큰
     * @return Mono<Boolean> - 저장 성공 여부를 비동기적으로 반환
     */
    @Override
    public Mono<Boolean> storeRefreshTokenMetadata(Long memberId, String refreshToken) {
        return storeRepository.storeRefreshTokenMetadata(memberId, refreshToken);
    }

}
