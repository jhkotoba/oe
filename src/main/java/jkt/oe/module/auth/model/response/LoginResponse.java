package jkt.oe.module.auth.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(staticName = "of")
@Deprecated
public class LoginResponse {
	
	private Long memberId;

}
