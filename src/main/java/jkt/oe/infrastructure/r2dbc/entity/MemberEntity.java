package jkt.oe.infrastructure.r2dbc.entity;

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
@Table("MEMBER_TEMP")
public class MemberEntity {
	
	@Id
	@Column("MEMBER_ID")
	private Long memberId;
	
}
