package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.game.Game;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SessionService {

    private Set<GameLobby> lobbies = new HashSet<>();
    private Map<Long, String> results = new HashMap<>();

    private GameRepository gameRepository;

    @Autowired
    public SessionService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void addLobby(GameLobby lobby) {
        lobbies.add(lobby);
    }

    public Optional<GameLobby> findLobbyById(long lobbyId) {
        return lobbies.stream().filter(lobby -> lobby.getLobbyId() == lobbyId).findFirst();
    }

    public boolean isGameFinished(long lobbyId) {
        return results.containsKey(lobbyId);
    }

    public String getResultForGame(long lobbyId) {
        return results.getOrDefault(lobbyId, "{\"winner\":\"notFound\"}");
    }

    public void terminateLobby(GameLobby lobby, String winner, String color) {
        lobbies.remove(lobby);
        results.put(lobby.getLobbyId(), "{\"name\":\"" + winner + "\", \"color\":\"" + color + "\"}");
    }

    void saveGameToDatabase(GameLobby lobby, String winner) {
        Game game = new Game();
        game.setGameId(Long.toString(lobby.getLobbyId()));
        game.setStartTime(lobby.getStartTime());
        game.setEndTime(LocalDateTime.now());
        game.setWinner(winner);
        game.setNumOfTurns(lobby.getNumOfTurns());
        gameRepository.save(game);
    }

    Set<GameLobby> getLobbies() {
        return lobbies;
    }
}
