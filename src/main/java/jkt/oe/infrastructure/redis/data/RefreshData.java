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
public class RefreshData {

	@NonNull
	private Long userNo;
	
	@NonNull
	private String issuedAt;   // ISO-8601 문자열
	
	@NonNull
    private String expiresAt;  // ISO-8601 문자열
	
	@NonNull
    private Boolean rotated;   // 회전 여부
	
	@NonNull
    private String kid;        // HMAC 키 버전
	
//	@NonNull
//    private String ua;         // 선택: 사용자 에이전트
//	
//	@NonNull
//    private String ip;         // 선택: 최초 발급 IP
//	
//	@NonNull
//    private String familyId;   // 선택: 회전 패밀리 식별자
	
}
