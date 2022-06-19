package de.bsi.reactive.webclient;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.bsi.reactive.webclient.model.Pet;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = RestTemplate.class)
@Slf4j
class DemoRestTemplateTest {
	
	@Autowired private RestTemplate restTemplate;
	
	@Test
	void synchronousRestTemplateHttpRequest() {
		var petStoreUri = UriComponentsBuilder
				.fromUriString(PetStoreService.PETSTORE_QUERY_URL)
				.build(PetStoreService.STATUS_AVAILABLE);
		
		log.info("BEFORE");
		Pet[] pets = restTemplate
				.getForEntity(petStoreUri, Pet[].class)
				.getBody();
		log.info("AFTER");
		
		assertThat(pets).isNotEmpty();
	}
}
