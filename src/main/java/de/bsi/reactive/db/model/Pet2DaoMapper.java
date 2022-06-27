package de.bsi.reactive.db.model;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import de.bsi.reactive.webclient.model.Pet;

@Component
public class Pet2DaoMapper implements Function<Pet, PetDAO> {

	@Override
	public PetDAO apply(Pet source) {
		return PetDAO.builder()
				.id(source.id())
				.name(source.name())
				.photoUrls(source.photoUrls())
				.status(source.status())
				.build();
	}
	
}
