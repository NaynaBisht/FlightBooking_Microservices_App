package com.user.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.user.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}