package jkt.oe.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
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
	
    /**
     * USER 테이블의 조회/저장 결과를 바인딩하는 생성자
     * @param userId   PK
     * @param loginId  로그인 아이디
     * @param email    이메일
     * @param password 비밀번호 해시
     * @param salt     솔트
     * @param isActive 사용여부
     * @param createdAt 등록일시
     * @param updatedAt 수정일시
     */
    @PersistenceCreator
    public UserEntity(Long userId, String loginId, String email, String password,
                      String salt, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
	
}
