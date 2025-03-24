package jkt.oe.persistence.ur.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface UserEntityRepository extends ReactiveCrudRepository<UserEntity, Long> {

	public Mono<UserEntity> findByUserId(String userId);
}
