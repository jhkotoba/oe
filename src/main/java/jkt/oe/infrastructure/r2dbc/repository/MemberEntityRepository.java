package jkt.oe.infrastructure.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import jkt.oe.infrastructure.r2dbc.entity.MemberEntity;



@Repository
public interface MemberEntityRepository extends ReactiveCrudRepository<MemberEntity, Long> {
	

}
