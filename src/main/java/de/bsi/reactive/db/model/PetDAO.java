package de.bsi.reactive.db.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "Pet")
@Data
@Builder
public class PetDAO {

	@Id private String id;
	private String name; 
	private List<String> photoUrls; 
	private String status;
	
}
