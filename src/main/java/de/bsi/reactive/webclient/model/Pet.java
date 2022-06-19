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
public record Pet(long id, String name, List<String> photoUrls, String status) {
	
	public static Pet createAvailablePetWithRandomId(String name) {
		long id = UUID.randomUUID().getMostSignificantBits();
		return new Pet(id, name, List.of(), "available");
	}
	
}
