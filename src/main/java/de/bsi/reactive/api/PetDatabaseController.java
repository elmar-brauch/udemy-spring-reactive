package de.bsi.reactive.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.bsi.reactive.db.PetReactiveDatabaseService;
import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/pet/db")
@Slf4j
public class PetDatabaseController {
	
	@Autowired private PetReactiveDatabaseService dbService;
		
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<Pet> createPetsInDatabase(@RequestBody final Flux<Pet> pets) {
		return dbService.saveAllPets(pets.doOnNext(p -> log.info("Passing Pet to database...")))
				.doFirst(() -> log.info("POST request received."));
	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<Pet> findPetsInDatabase(@RequestParam final String name) {
		return dbService.readAllPetsWith(name);
	}
}
