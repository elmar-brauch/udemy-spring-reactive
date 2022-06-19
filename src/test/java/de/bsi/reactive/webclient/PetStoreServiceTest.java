package de.bsi.reactive.webclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

@SpringBootTest(classes = PetStoreService.class)
@Slf4j
class PetStoreServiceTest {

	@Autowired private PetStoreService service;
	
	private final List<Pet> pets = new ArrayList<>();
	
	@Test
	void requestAvailablePetsTest() {
		assertThat(service.requestAvailablePets()).isNotEmpty();
	}
	
	@Test
	void createPetTest() {
		log.info("BEFORE");
		assertNotNull(service.createPet("Hansi"));
		log.info("AFTER");
	}
	
	@Test
	void createPetReactiveTest() {
		final String petName = "Hansi der " + System.currentTimeMillis();
		log.info("BEFORE");
		Disposable petCreation = service.createPetReactive(petName)
				.subscribe(p -> pets.add(p));
		log.info("AFTER");
		assertTrue(pets.isEmpty());
		Awaitility.await().until(() -> petCreation.isDisposed());
		assertEquals(1, pets.size());
	}
	
	@Test
	void reactiveTest() throws InterruptedException {
		Disposable petsRead = service.requestAvailablePetsReactive()
				.subscribe(p -> pets.add(p));
		assertTrue(pets.size() < 5);
		
		Awaitility.await().until(() -> petsRead.isDisposed());
		assertEquals(5, pets.size());
	}
}
