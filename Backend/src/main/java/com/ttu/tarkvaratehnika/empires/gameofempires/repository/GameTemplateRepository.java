package com.ttu.tarkvaratehnika.empires.gameofempires.repository;

import com.ttu.tarkvaratehnika.empires.gameofempires.template.GameTemplate;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface GameTemplateRepository extends CrudRepository<GameTemplate, Long> {
}
