package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.game.Game;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.Properties;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.SessionSettings;
import com.ttu.tarkvaratehnika.empires.gameofempires.messagekeys.MessageKeys;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SessionService {

    private Set<GameLobby> lobbies = new HashSet<>();
    private Gson gson = new Gson();

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
        return gameRepository.getGameByGameId(lobbyId).isPresent();
    }

    public Properties getLobbySettings(long lobbyId) throws IllegalArgumentException {
        Optional<GameLobby> searched = findLobbyById(lobbyId);
        if (searched.isPresent()) {
            return searched.get().getProperties();
        } else {
            throw new IllegalArgumentException(MessageKeys.LOBBY_NOT_FOUND);
        }
    }

    public void setLobbySettings(Properties properties) throws IllegalArgumentException {
        Optional<GameLobby> searched = findLobbyById(properties.getLobbyId());
        if (searched.isPresent()) {
            GameLobby lobby = searched.get();
            if (properties.getMaxPlayersNumber() > SessionSettings.DEFAULT_MAX_USERS || properties.getMaxPlayersNumber() <= 1) {
                properties.setMaxPlayersNumber(SessionSettings.DEFAULT_NUM_OF_PLAYERS);
            }
            if (properties.getIterationsNumber() > SessionSettings.MAX_TURNS && properties.getIterationsNumber() < 1) {
                properties.setIterationsNumber(SessionSettings.DEFAULT_NUM_OF_TURNS);
            }
            if (properties.getMapName() == null || properties.getMapName().isEmpty()) {
                properties.setMapName(SessionSettings.DEFAULT_MAP);
            }
            lobby.setProperties(properties);
        } else {
            throw new IllegalArgumentException(MessageKeys.LOBBY_NOT_FOUND);
        }
    }

    public String getResultForGame(long lobbyId) {
        Optional<Game> searched = gameRepository.getGameByGameId(lobbyId);
        JsonObject response = new JsonObject();
        if (searched.isPresent()) {
            Game game = searched.get();
            response.addProperty("name", game.getWinner());
            response.addProperty("color", game.getWinnerColor());
            response.addProperty("status", "success");
            response.addProperty("message", MessageKeys.NULL);
        } else {
            response.addProperty("winner", MessageKeys.NULL);
            response.addProperty("color", MessageKeys.NULL);
            response.addProperty("status", "failed");
            response.addProperty("message", MessageKeys.NO_GAME_FOUND);
        }
        return gson.toJson(response);
    }

    public void terminateLobby(GameLobby lobby, String winner, String color) {
        lobbies.remove(lobby);
        saveGameToDatabase(lobby, winner, color);
    }

    public void terminateLobbyWithoutSave(GameLobby lobby) {
        lobbies.remove(lobby);
    }

    void saveGameToDatabase(GameLobby lobby, String winner, String color) {
        Game game = new Game();
        game.setGameId(lobby.getLobbyId());
        game.setStartTime(Timestamp.valueOf(lobby.getStartTime()));
        game.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        game.setWinner(winner);
        game.setWinnerColor(color);
        game.setNumOfTurns(lobby.getNumOfTurns());
        gameRepository.save(game);
    }

    Set<GameLobby> getLobbies() {
        return lobbies;
    }
}
