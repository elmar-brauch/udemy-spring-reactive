package de.bsi.reactive.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bsi.reactive.db.model.Dao2PetMapper;
import de.bsi.reactive.db.model.Pet2DaoMapper;
import de.bsi.reactive.db.model.PetDAO;
import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class PetReactiveDatabaseService {
	
	@Autowired private PetRepository repo;
	@Autowired private Pet2DaoMapper toDaoMapper;
	@Autowired private Dao2PetMapper toPetMapper;
	
	public Flux<Pet> readAllPets() {
		return repo.findAll()
			.doOnNext(p -> log.info("Found pet {} in MongoDB.", p.getName()))
			.map(toPetMapper);
	}
	
	public Flux<Pet> save(Flux<Pet> newPets) {
		Flux<PetDAO> petDaos = newPets.map(toDaoMapper);
		return repo.insert(petDaos).map(toPetMapper);
	}
	
}
