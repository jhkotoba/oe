package jkt.oe.persistence.entity;

import org.springframework.data.annotation.Id;
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
@Table("ROLE_SCOPE")
@Deprecated
public class RoleScopeEntity {
	
	@Id
	@Column("ROLE_SCOPE_ID")
	private Long roleScopeId;
	
	@Column("ROLE_ID")
	private Long roleId;
	
	@Column("SCOPE_ID")
	private Long scopeId;
	
	@PersistenceCreator
	public RoleScopeEntity(Long roleScopeId, Long roleId, Long scopeId) {
		this.roleScopeId =  roleScopeId;
		this.roleId = roleId;
		this.scopeId = scopeId;
	}
}
