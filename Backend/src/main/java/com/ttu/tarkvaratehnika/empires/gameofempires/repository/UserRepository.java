package com.ttu.tarkvaratehnika.empires.gameofempires.repository;

import com.ttu.tarkvaratehnika.empires.gameofempires.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByName(String name);
}
