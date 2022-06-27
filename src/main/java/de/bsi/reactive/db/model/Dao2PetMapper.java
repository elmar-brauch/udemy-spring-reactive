package de.bsi.reactive.db.model;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import de.bsi.reactive.webclient.model.Pet;

@Component
public class Dao2PetMapper implements Function<PetDAO, Pet> {

	@Override
	public Pet apply(PetDAO source) {
		return new Pet(source.getId(), source.getName(), 
				source.getPhotoUrls(), source.getStatus());
	}

}
