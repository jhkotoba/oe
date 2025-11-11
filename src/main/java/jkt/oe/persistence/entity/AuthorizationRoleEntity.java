package jkt.oe.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Immutable;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;

/**
 * ROLE 과 매칭되는 엔티티 클래스
 */
@Getter
@Builder
@Immutable
@Table("AUTHORIZATION_ROLE")
public class AuthorizationRoleEntity {
	
	@Id
	@Column("ROLE_ID")
	private Long scopeId;
	
	@Column("ROLE_NAME")
	private String roleName;

	@Column("ROLE_DESCRIPTION")
	private String roleDescription;	
	
	@Column("IS_ACTIVE")
	private boolean isActive;
	
	@Column("IS_DEPRECATED")
	private boolean isDeprecated;
	
	@Column("INTRODUCED_AT")
	private LocalDateTime introducedAt;
	
	@Column("DEPRECATED_AT")
	private LocalDateTime deprecatedAt;
	
	@PersistenceCreator
	public AuthorizationRoleEntity(Long scopeId, String scopeCode, String roleDescription, boolean isActive, boolean isDeprecated, 
			LocalDateTime introducedAt, LocalDateTime deprecatedAt) {
		this.scopeId =  scopeId;		
		this.roleDescription = roleDescription;
		this.isActive = isActive;
		this.isDeprecated = isDeprecated;
		this.introducedAt = introducedAt;
		this.deprecatedAt = deprecatedAt;
	}
	
}
