package de.bsi.reactive.db;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import de.bsi.reactive.db.model.PetDAO;
import reactor.core.publisher.Flux;

public interface PetRepository extends ReactiveMongoRepository<PetDAO, String> {
	
	Flux<PetDAO> findByNameLike(String name);
	
}
