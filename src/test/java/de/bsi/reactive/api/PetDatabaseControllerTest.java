package de.bsi.reactive.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import de.bsi.reactive.db.PetRepository;
import de.bsi.reactive.db.model.PetDAO;
import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class PetDatabaseControllerTest {

	private static final String PET_NAME_1 = "Bambi";
	private static final String PET_NAME_2 = "Klopfer";
	// Check WebTestClient for real tests.
	private WebClient client = WebClient.create("http://localhost:8080/pet/db");

	@BeforeEach
	void setup(@Autowired PetRepository repo) {
		repo.deleteAll().block();
		var pet1 = PetDAO.builder().name(PET_NAME_2).build();
		repo.insert(pet1).block();	
	}
	
	@Test
	void readFluxWithWebClientFromDatabaseTest() {
		var foundPetBySubstring = client.get()
				.uri("?name=opf")
				.retrieve()
				.bodyToFlux(Pet.class)
				.blockFirst();
				
		assertEquals(PET_NAME_2, foundPetBySubstring.name());
	}
	
	@Test
	void sendFluxWithWebClientToDatabaseTest() {
		var requestStream = Stream
				.generate(() -> Pet.createPet(PET_NAME_1))
				.limit(5);
		var requestFlux = Flux.fromStream(requestStream)
				.delayElements(Duration.ofSeconds(1));
		
		var responseFlux = client.post()
				.contentType(MediaType.APPLICATION_NDJSON)
				.body(requestFlux, Pet.class)
				.retrieve()
				.bodyToFlux(Pet.class)
				.doOnNext(p -> log.info("Stored Pet received from server."));
				
		assertTrue(responseFlux.all(p -> PET_NAME_1.equals(p.name())).block());
	}
	
}
