package com.ttu.tarkvaratehnika.empires.gameofempires.repository;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GameMapRepository extends CrudRepository<GameMap, Long> {

    Optional<GameMap> getGameMapByName(String name);

    @Query("SELECT m FROM GameMap m WHERE m.author=:author OR :author=''")
    Iterable<GameMap> getGameMaps(@Param(value = "author") String author);
}
