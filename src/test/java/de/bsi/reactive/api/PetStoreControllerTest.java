package de.bsi.reactive.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.reactive.function.client.WebClient;

import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Slf4j
class PetStoreControllerTest {
	
	// Check WebTestClient for real tests.
	private WebClient client = WebClient.create("http://localhost:8080");
	
	private List<Pet> pets = new ArrayList<>();
	
	@Test
	void monoWithDummyTest() {
		var disposable = client.post()
				.uri(PetStoreController.URL_PATH_DUMMY)
				.bodyValue(Pet.createPet("Hansi"))
				.retrieve()
				.bodyToMono(Pet.class)
				.subscribe(pet -> pets.add(pet));
		
		checkResponse(disposable);
	}
	
	private void checkResponse(Disposable disposable) {
		assertThat(pets).isEmpty();
		log.info("Reactive Request send.");
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals("Hansi", pets.get(0).name());	
	}
	
	@Test
	void monoWithPetstoreTest() {
		var disposable = client.post()
				.uri(PetStoreController.URL_PATH_REAL)
				.bodyValue(Pet.createPet("Hansi"))
				.retrieve()
				.bodyToMono(Pet.class)
				.subscribe(pet -> pets.add(pet));
		
		checkResponse(disposable);
	}
	
	@Test
	void fluxWithDummyTest() {
		var disposable = client.get()
				.uri(PetStoreController.URL_PATH_DUMMY)
				.retrieve()
				.bodyToFlux(Pet.class)
				.subscribe(pet -> {
					log.info("Client received {}.", pet.name());
					pets.add(pet);	
				});
		
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals(3, pets.size());	
	}
	
	@Test
	void fluxWithPetstoreTest() {
		var disposable = client.get()
				.uri(PetStoreController.URL_PATH_REAL)
				.retrieve()
				.bodyToFlux(Pet.class)
				.subscribe(pet -> {
					log.info("Client received {}.", pet.name());
					pets.add(pet);	
				});
		
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals(5, pets.size());	
	}

}
