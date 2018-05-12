package com.ttu.tarkvaratehnika.empires.gameofempires.repository;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameMapRepository extends CrudRepository<GameMap, Long> {

    Optional<GameMap> getGameMapByName(String name);
}
