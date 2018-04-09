package com.ttu.tarkvaratehnika.empires.gameofempires.repository;

import com.ttu.tarkvaratehnika.empires.gameofempires.game.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, String> {

    Game getGameByGameId(String gameId);
}
