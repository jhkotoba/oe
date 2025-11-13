package jkt.oe.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;

/**
 * MEMBER_SERVICE_ROLE 과 매칭되는 엔티티 클래스
 */
@Getter
@Builder
@Table("MEMBER_SERVICE_ROLE")
public class MemberServiceRoleEntity {
	
	@Id
	@Column("MEMBER_ROLE_ID")
	private Long memberRoleId;
	
	@Column("MEMBER_ID")
	private Long memberId;
	
	@Column("SERVICE_ID")
	private Long serviceId;
	
	@Column("ROLE_ID")
	private Long roleId;
	
	@Column("VALID_FROM_AT")
	private LocalDateTime validFromAt;
	
	@Column("VALID_UNTIL_AT")
	private LocalDateTime validUntilAt;
	
	@Column("IS_ACTIVE")
	private boolean isActive;
	
	@Column("CREATED_AT")
	private LocalDateTime createdAt;
	
	@Column("UPDATED_AT")
	private LocalDateTime updatedAt;
	
	@PersistenceCreator
	public MemberServiceRoleEntity(Long memberRoleId, Long memberId, Long serviceId, Long roleId,
			LocalDateTime validFromAt, LocalDateTime validUntilAt, 
			boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {		
		this.memberRoleId	= memberRoleId;		
		this.memberId = memberId;
		this.serviceId = serviceId;		
		this.roleId = roleId;
		this.validFromAt = validFromAt;
		this.validUntilAt = validUntilAt;
		this.isActive = isActive;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
