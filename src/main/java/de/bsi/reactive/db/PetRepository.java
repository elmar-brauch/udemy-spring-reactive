package de.bsi.reactive.db;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import de.bsi.reactive.db.model.PetDAO;

public interface PetRepository extends ReactiveMongoRepository<PetDAO, String>{

}
