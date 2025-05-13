package jkt.oe.module.auth.token.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(staticName = "of")
@Deprecated
public class RefreshTokenCreateData {

	@NonNull
	private Long userNo;
	
	@NonNull
	private String id;
	
	@NonNull
	private String userAgent;
	
	
}
