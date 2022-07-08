package de.bsi.reactive.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.bsi.reactive.webclient.PetStoreService;
import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class PetStoreController {
	
	static final String URL_PATH_DUMMY = "/pet/dummy";
	static final String URL_PATH_STORE = "/pet/store";

	@Autowired private PetStoreService apiService;
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(URL_PATH_DUMMY)
	public Mono<Pet> createDummyPet(@RequestBody final Pet newPet) {
		log.info("Reactive POST received.");
		return Mono.fromSupplier(() -> {
			log.info("Supplier triggered.");
			return timeConsumingPetCreation(newPet.name());	
		});
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(URL_PATH_STORE)
	public Mono<Pet> createPet(@RequestBody final Pet newPet) {
		Mono<Pet> result = apiService.createPetReactive(newPet.name());
		log.info("Thread does not wait.");
		return result;
	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = URL_PATH_DUMMY,
			produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<Pet> findDummyPets() {
		var petNames = List.of("Hopsi", "Flocke", "Blacky");
		var petStream = petNames.stream()
				.map(this::timeConsumingPetCreation);
		return Flux.fromStream(petStream);
	}
	
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(path = URL_PATH_STORE,
			produces = MediaType.APPLICATION_NDJSON_VALUE)
	public Flux<Pet> findAvailablePets() {
		return apiService.requestAvailablePetsReactive()
				.doOnComplete(() -> log.info("Server completed Flux."));
	}
	
	private Pet timeConsumingPetCreation(final String name) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// ignored
		}
		log.info("Server created {}.", name);
		return Pet.createAvailablePetWithRandomId(name);	
	}
	
}
