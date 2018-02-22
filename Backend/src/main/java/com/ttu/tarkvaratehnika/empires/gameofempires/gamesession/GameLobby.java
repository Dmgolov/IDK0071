package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.ttu.tarkvaratehnika.empires.gameofempires.controller.SessionController;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.util.*;

public class GameLobby {

    private static long id = 0;

    private SessionController controller;
    private final long lobbyId;

    private GameField gameField;

    private String lobbyName;
    private String lobbyPass;
    private Set<Nation> nations = new HashSet<>();
    private List<String> availableColors = new ArrayList<>(Arrays.asList("yellow", "red", "purple", "brown"));
    private int waiting = 0;

    public GameLobby(SessionController controller) {
        this.controller = controller;
        lobbyId = ++id;
    }

    public boolean startSinglePlayerSession() {
        for (int i = 1; i <= 3; i++) {
            enterSession("bot" + i);
            nations.forEach(nation -> nation.setReady(true));
        }
        return startSession();
    }

    public boolean startSession() {
        if (nations.stream().filter(Nation::isReady).count() == SessionSettings.DEFAULT_MAX_USERS) {
            nations.stream().filter(nation -> !nation.hasSelectedPersonType()).forEach(Nation::setDefault);
            nations.forEach(Nation::run);
            return true;
        } else {
            return false;
        }
    }

    public boolean setNationStats(String username, int vitality, int dexterity, int intelligence,
                                  int growthRate, int strength, int luck) {
        Optional<Nation> chosen = nations.stream().filter(nation -> nation.getUsername().equals(username)).findFirst();
        return chosen.isPresent()
                && chosen.get().setPersonWithStats(vitality, dexterity, intelligence, growthRate, strength, luck);
    }

    public boolean setNationStatsByTemplate(String username, String templateName) {
        Optional<Nation> chosen = nations.stream().filter(nation -> nation.getUsername().equals(username)).findFirst();
        return chosen.isPresent() && chosen.get().useTemplateForPerson(templateName);
    }

    public Optional<Nation> checkWinner() {
        long active = nations.stream().filter(nation -> nation.getNumOfPeople() > 0).count();
        if (active == 1) {
            return nations.stream().filter(nation -> nation.getNumOfPeople() > 0).findFirst();
        }
        return Optional.empty();
    }

    public boolean enterSession(String username) {
        Nation nation = new Nation(username, availableColors.get(0), this);
        availableColors.remove(0);
        return nations.add(nation);
    }

    public boolean leaveSession(String username) {
        Optional<Nation> nation = nations.stream().filter(active -> active.getUsername().equals(username)).findFirst();
        if (nation.isPresent()) {
            availableColors.add(nation.get().getTeamColor());
            return nations.remove(nation.get());
        }
        return false;
    }

    public void readyCheck(String username, boolean ready) {
        nations.stream()
                .filter(nation -> nation.getUsername().equals(username))
                .findFirst().ifPresent(nation -> nation.setReady(ready));

    }

    public void endTurn() {
        waiting++;
        if (waiting >= 4) {
            if (checkWinner().isPresent()) {
                controller.sendSessionResult(checkWinner().get().getUsername(), lobbyId);
                return;
            }
            waiting = 0;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public GameField getGameField() {
        return gameField;
    }

    public void setGameField(GameField gameField) {
        this.gameField = gameField;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getLobbyPass() {
        return lobbyPass;
    }

    public void setLobbyPass(String lobbyPass) {
        this.lobbyPass = lobbyPass;
    }

    public long getLobbyId() {
        return lobbyId;
    }
}
