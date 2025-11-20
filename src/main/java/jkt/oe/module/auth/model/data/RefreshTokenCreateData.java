package jkt.oe.module.auth.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * AccessToken 생성을 위해 필요한 데이터를 담는 DTO 클래스
 */
@Getter
@Setter
@Builder
@AllArgsConstructor(staticName = "of")
@Deprecated
public class RefreshTokenCreateData {

	/**
     * UUID
     */
	@NonNull
	private String uuid;
	
	/**
	 * 사용자 번호
	 */
	@NonNull
	private Long memberNo;
	
	/**
     * 사용자 아이디
     */
	@NonNull
	private String memberId;
}
