package com.ttu.tarkvaratehnika.empires.gameofempires.repository;

import com.ttu.tarkvaratehnika.empires.gameofempires.game.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GameRepository extends CrudRepository<Game, String> {

    Optional<Game> getGameByGameId(long gameId);

    @Query("SELECT Max(g.gameId) FROM Game g")
    Optional<Long> getMaxId();
}
