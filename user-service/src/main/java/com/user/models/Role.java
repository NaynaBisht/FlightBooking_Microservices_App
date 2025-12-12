package com.user.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "roles")
@Data
@NoArgsConstructor
public class Role {
	@Id
	private String id;

	private ERole name;

	public Role(ERole name) {
		this.name = name;
	}
}