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
 * SCOPE 과 매칭되는 엔티티 클래스
 */
@Getter
@Builder
@Immutable
@Table("SCOPE")
@Deprecated
public class AuthorizationScopeEntity {
	
	@Id
	@Column("SCOPE_ID")
	private Long scopeId;
	
	@Column("SCOPE_CODE")
	private String scopeCode;
	
	@Column("SCOPE_DESCRIPTION")
	private String scopeDescription;
	
	@Column("IS_ACTIVE")
	private boolean isActive;
	
	@Column("IS_DEPRECATED")
	private boolean isDeprecated;
	
	@Column("INTRODUCED_AT")
	private LocalDateTime introducedAt;
	
	@Column("DEPRECATED_AT")
	private LocalDateTime deprecatedAt;
	
	@PersistenceCreator
	public AuthorizationScopeEntity(Long scopeId, String scopeCode, String scopeDescription, boolean isActive, boolean isDeprecated, 
			LocalDateTime introducedAt, LocalDateTime deprecatedAt) {
		this.scopeId =  scopeId;
		this.scopeCode = scopeCode;
		this.scopeDescription = scopeDescription;
		this.isActive = isActive;
		this.isDeprecated = isDeprecated;
		this.introducedAt = introducedAt;
		this.deprecatedAt = deprecatedAt;
	}
	
}
