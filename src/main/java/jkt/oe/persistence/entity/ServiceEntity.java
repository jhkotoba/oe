package jkt.oe.persistence.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Getter;

/**
 * SERVICE 과 매칭되는 엔티티 클래스
 */
@Getter
@Builder
@Table("SERVICE")
@Deprecated
public class ServiceEntity {
	
	@Id
	@Column("SERVICE_ID")
	private Long serviceId;
	
	@Column("SERVICE_NAME")
	private String serviceName;
	
	@Column("IS_AUTHORIZATION_REQUIRED")
	private boolean isAuthorizationRequired;
	
	@Column("IS_ACTIVE")
	private boolean isActive;
	
	@Column("CREATED_AT")
	private LocalDateTime createdAt;
	
	@PersistenceCreator
	public ServiceEntity(Long roleScopeId, String serviceName, 
			boolean isAuthorizationRequired, boolean isActive, LocalDateTime createdAt) {
		this.serviceId =  roleScopeId;
		this.serviceName = serviceName;
		this.isAuthorizationRequired = isAuthorizationRequired;
		this.isActive = isActive;
		this.createdAt = createdAt;
	}
}
