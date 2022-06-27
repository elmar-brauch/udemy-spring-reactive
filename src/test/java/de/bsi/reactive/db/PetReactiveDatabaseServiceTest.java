package de.bsi.reactive.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;

import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@DataMongoTest
@ComponentScan("de.bsi.reactive.db")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class PetReactiveDatabaseServiceTest {

	@Autowired private PetReactiveDatabaseService service;
	
	@Test
	@Order(1)
	void savePetsTest() {
		Flux<Pet> savedPets = service.save(Flux.fromStream(generatePets()));
		
		assertTrue(savedPets
				.all(p -> p.id() != null && p.name().startsWith("Hansi"))
				.block());
	}
	
	private Stream<Pet> generatePets() {
		IntFunction<Pet> int2Pet = i -> new Pet(
				null, "Hansi der %d.".formatted(i), 
				List.of("http://hansi.pic.de"), "available"); 
		
		return IntStream.range(1, 11)
			.mapToObj(int2Pet);
	}
	
	@Test
	@Order(2)
	void savePetsTest2() {
		Flux<Pet> pets = service.readAllPets();
		
		assertEquals(10, pets.count().block());
	}

}
