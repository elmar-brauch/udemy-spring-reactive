package de.bsi.reactive.webclient.model;

import java.util.List;
import java.util.UUID;

/**
 * Record to parse JSON response of PetStore API.	
 * Simplified example of data type Pet.
 *  {
 *    "id":9223372016900027917,
 *    "name":"doggie",
 *    "photoUrls":["string"],
 *    "status":"available"
 *  }
 */
public record Pet(String id, String name, List<String> photoUrls, String status) {
	
	public static Pet createPet(String name) {
		return new Pet(null, name, List.of(), null);
	}
	
	public static Pet createAvailablePetWithRandomId(String name) {
		String id = "" + UUID.randomUUID().getMostSignificantBits();
		return new Pet(id, name, List.of(), "available");
	}
	
}
