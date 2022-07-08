package de.bsi.reactive.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import de.bsi.reactive.db.model.PetDAO;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@DataMongoTest
@EnableReactiveMongoRepositories
@Slf4j
class PetRepositoryTest {
	
	private static final String PET_NAME_1 = "Bambi";
	private static final String PET_NAME_2 = "Klopfer";
	private static final List<String> PET_NAMES = List.of(PET_NAME_1, PET_NAME_2);

	@Autowired private PetRepository repo;
	
	@BeforeEach
	void setup() {
		repo.deleteAll().block();
		var pet1 = PetDAO.builder().name(PET_NAME_1).build();
		repo.insert(pet1).block();
	}
	
	@Test
	void simpleReactiveWriteAndCountTest() {
		var pet2 = PetDAO.builder().name(PET_NAME_2).build();
		
		var disposable = repo.insert(pet2).subscribe(
				p -> log.info("Pet with id {} created.", p.getId()));
	
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals(2, repo.count().block());
	}

	@Test
	void simpleReactiveReadTest() {
		final var foundPets = new ArrayList<PetDAO>();
		
		var disposable = repo.findAll().subscribe(p -> foundPets.add(p));
		
		Awaitility.await().until(() -> disposable.isDisposed());
		assertEquals(1, foundPets.size());
	}
	
	@Test
	void writeFluxTest() {
		var inputStream = generatePets(10, List.of(PET_NAME_1));
		
		repo.insert(Flux.fromStream(inputStream)).blockLast();
		
		assertEquals(11, repo.count().block());
	}
	
	@Test
	void readWithSubstringTest() {
		var petStream = generatePets(100, PET_NAMES);
		repo.insert(Flux.fromStream(petStream)).blockLast();
		
		Flux<PetDAO> foundPets = repo.findByNameLike("opf");
		
		assertEquals(100, foundPets.count().block());
	}
	
	private Stream<PetDAO> generatePets(int amountPerName, List<String> petNames) {
		IntFunction<List<PetDAO>> int2Pet = i -> {
			var pets = new ArrayList<PetDAO>();
			petNames.forEach(name -> pets.add(PetDAO.builder()
					.name("%s der %d.".formatted(name, i))
					.photoUrls(List.of("http://%s.pic.de".formatted(name)))
					.status("available").build()));
			return pets;
		};			
		return IntStream.range(1, 1 + amountPerName)
				.mapToObj(int2Pet)
				.flatMap(pets -> pets.stream());
	}

}
