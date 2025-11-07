package jkt.oe.persistence.ur.user;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;

/**
 * UR_USER과 매칭되는 엔티티 클래스
 */
@Getter
@Builder
@Table("USER")
public class UserEntity {
	
	@Id
	@Column("USER_ID")
	private Long userId;
	
	@Column("LOGIN_ID")
	private String loginId;
	
	@Column("EMAIL")
	private String email;
	
	@Column("PASSWORD") 
	private String password;
	
	@Column("SALT")
	private String salt;
	
	@Column("IS_ACTIVE")
	private boolean isActive;
	
	@Column("CREATED_AT")
	private LocalDateTime createdAt;
	
	@Column("UPDATED_AT")
	private LocalDateTime updatedAt;
	
}
