package jkt.oe.application.authentication.token.port.out;

import reactor.core.publisher.Mono;

public interface StoreRefreshTokenMetadataPort {

    Mono<Boolean> storeRefreshTokenMetadata(Long memberId, String refreshToken);

}
