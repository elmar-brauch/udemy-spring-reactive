package de.bsi.reactive.webclient;

import java.time.Duration;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class PetStoreService {

	static final String STATUS_AVAILABLE = "available";
	static final String PETSTORE_CREATE_URL = "https://petstore.swagger.io/v2/pet";
	static final String PETSTORE_QUERY_URL = PETSTORE_CREATE_URL + "/findByStatus?status={0}";
	
	private WebClient client = WebClient.create();
	
	public Pet[] requestAvailablePets() {
		var responseBody = client.get()
				.uri(PETSTORE_QUERY_URL, STATUS_AVAILABLE)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		if (responseBody != null)
			log.info(responseBody.substring(0, 200));
		
		return client.get()
				.uri(PETSTORE_QUERY_URL, STATUS_AVAILABLE)
				.exchangeToMono(response -> {
					log.info("Response code: {}", response.statusCode());
					return response.bodyToMono(Pet[].class);
				}).block();
	}
	
	public Pet createPet(String name) {
		var requestBody = Pet.createAvailablePetWithRandomId(name);
		
		return client.post()
				.uri(PETSTORE_CREATE_URL)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(Pet.class)
				.doOnSuccess(pet -> log.info("Service received Pet with id {}.", pet.id()))
				.block();
	}
	
	private Predicate<Throwable> isRetryable = 
			t -> t instanceof WebClientResponseException responseEx 
			&& responseEx.getStatusCode().is5xxServerError();
	
	public Mono<Pet> createPetReactive(String name) {
		var requestBody = Pet.createAvailablePetWithRandomId(name);

		return client.post()
				.uri(PETSTORE_CREATE_URL)
				.bodyValue(requestBody)
				.retrieve()
				.bodyToMono(Pet.class)
				.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
						.filter(isRetryable))
				.doOnSuccess(pet -> log.info(
						"Pet with id {} created.", pet.id()));
	}
	
	public Flux<Pet> requestAvailablePetsReactive() {
		return client.get()
				.uri(PETSTORE_QUERY_URL, STATUS_AVAILABLE)
				.retrieve()
				.bodyToFlux(Pet.class)
				.takeLast(5)
				.doOnEach(signal -> {
					if (signal.hasValue())
						log.info("Service received {}.", signal.get().name());
				});
	}
	
}
