package jkt.oe.infrastructure.r2dbc.member;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;

/**
 * MEMBER 과 매칭되는 엔티티 클래스
 */
@Getter
@Builder
@Table("MEMBER")
public class MemberEntity {

	@Id
	@Column("MEMBER_ID")
	private Long memberId;

	@Column("LOGIN_ID")
	private String loginId;

	@Column("EMAIL")
	private String email;

	@Column("PASSWORD")
	private String password;

	@Column("SALT")
	private String salt;

	@Column("IS_ACTIVE")
	private String isActive;

	@Column("CREATED_AT")
	private String createdAt;

	@Column("UPDATED_AT")
	private String updatedAt;

}
