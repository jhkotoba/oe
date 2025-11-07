package jkt.oe.module.auth.model.data;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 정보 데이터 객체
 */
@Getter
@Setter
@Builder
public class UserData {
		
	private Long userId;
		
	private String loginId;
	
	private String email;
	
	private String password;
	
	private String salt;
	
	private boolean isActive;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
