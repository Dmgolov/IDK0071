package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.ttu.tarkvaratehnika.empires.gameofempires.controller.SessionController;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.player.Player;

import java.util.*;

//TODO: implement
//Instance of single session. Check game state, finds winner, etc.
public class GameSession {

    public static long id = 0;

    private SessionController controller;
    private final long sessionId;

    private GameField gameField;

    private Set<Player> players = new HashSet<>();

    public GameSession(SessionController controller) {
        this.controller = controller;
        sessionId = id++;
    }

    public void calculateGameState() {

    }

    public Optional<Player> checkWinner() {
        return Optional.empty();
    }

    public boolean enterGame(Player player) {
        return players.add(player);
    }

    public boolean leaveSession(Player player) {
        return players.remove(player);
    }

    public boolean isSessionEmpty() {
        return players.size() == 0;
    }

    public GameField getGameField() {
        return gameField;
    }

    public void setGameField(GameField gameField) {
        this.gameField = gameField;
    }
}
