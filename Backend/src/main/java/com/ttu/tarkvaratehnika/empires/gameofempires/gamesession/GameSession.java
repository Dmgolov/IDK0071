package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.ttu.tarkvaratehnika.empires.gameofempires.controller.SessionController;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.util.*;

//TODO: implement
//Instance of single session. Check game state, finds winner, etc.
public class GameSession {

    public static long id = 0;

    private SessionController controller;
    private final long sessionId;

    private GameField gameField;

    private String sessionName;
    private Set<Nation> nations = new HashSet<>();

    public GameSession(SessionController controller) {
        this.controller = controller;
        sessionId = id++;
    }

    public boolean setNationStats(long id, int vitality, int dexterity, int intelligence,
                                  int growthRate, int strength, int luck) {
        Optional<Nation> chosen = nations.stream().filter(nation -> nation.getId() == id).findFirst();
        return chosen.isPresent()
                && chosen.get().setPersonWithStats(vitality, dexterity, intelligence, growthRate, strength, luck);
    }

    public boolean setNationStatsByTemplate(long id, String templateName) {
        Optional<Nation> chosen = nations.stream().filter(nation -> nation.getId() == id).findFirst();
        return chosen.isPresent() && chosen.get().useTemplateForPerson(templateName);
    }

    public void calculateGameState() {

    }

    public Optional<Nation> checkWinner() {
        return Optional.empty();
    }

    public boolean enterGame(Nation nation) {
        return nations.add(nation);
    }

    public boolean leaveSession(Nation nation) {
        return nations.remove(nation);
    }

    public boolean isSessionEmpty() {
        return nations.size() == 0;
    }

    public GameField getGameField() {
        return gameField;
    }

    public void setGameField(GameField gameField) {
        this.gameField = gameField;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
}
