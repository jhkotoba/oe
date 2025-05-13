package jkt.oe.infrastructure.redis.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(staticName = "of")
public class StoreRefreshTokenData {

	@NonNull
	private Long userNo;
	
	@NonNull
	private String refreshToken;
	
	@NonNull
	private String ip;
	
	@NonNull
	private String userAgent;
	
}
