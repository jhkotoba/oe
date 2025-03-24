package jkt.oe.persistence.ur.user;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;

@Getter
@Table("UR_USER")
public class UserEntity {
	
	@Id
	@Column("USER_NO")
	private Long userNo;
	
	@Column("USER_ID")
	private String userId;
	
	@Column("PASSWORD") 
	private String password;
	
	@Column("USE_YN")
	private String useYn;
	
	@Column("INS_DTTM")
	private LocalDateTime insDttm;
	
	@Column("SALT")
	private String salt;

}
