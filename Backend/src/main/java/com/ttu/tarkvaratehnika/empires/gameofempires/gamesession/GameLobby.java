package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.ttu.tarkvaratehnika.empires.gameofempires.controller.LobbyController;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.util.*;
import java.util.stream.Collectors;

public class GameLobby {

    private static long id = 0;
    private int numOfTurns = 0;

    private LobbyController controller;
    private final long lobbyId;

    private GameField gameField;

    private String lobbyName;
    private String lobbyPass;
    private Set<Nation> nations = new HashSet<>();
    private Map<Coordinates, Person> cellsToUpdate = new HashMap<>();
    private Map<Coordinates, Person> lastUpdate = new HashMap<>();
    private List<String> availableColors = new ArrayList<>(Arrays.asList("yellow", "red", "purple", "brown"));
    private int waiting = 0;

    public GameLobby(LobbyController controller) {
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
            nations.stream().filter(nation -> !nation.hasSelectedPersonType()).forEach(Nation::setDefaultPerson);
            nations.forEach(nation -> nation.getPerson().setStartingLocation());
            nations.forEach(Nation::run);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Nation> checkWinner() {
        long active = nations.stream().filter(Nation::isExtinct).count();
        if (active == 1) {
            return nations.stream().filter(Nation::isExtinct).findFirst();
        } else if (active == 0) {
            return Optional.of(new Nation("none", null, this));
        } else if (numOfTurns >= SessionSettings.MAX_TURNS) {
            return nations.stream().max(Comparator.comparingInt(Nation::getNumOfPeople));
        }
        return Optional.empty();
    }

    public boolean enterSession(String username) {
        if (nations.size() < SessionSettings.DEFAULT_MAX_USERS) {
            Nation nation = new Nation(username, availableColors.get(0), this);
            availableColors.remove(0);
            return nations.add(nation);
        }
        return false;
    }

    public boolean leaveSession(String username) {
        Optional<Nation> nation = nations.stream().filter(active -> active.getUsername().equals(username)).findFirst();
        if (nation.isPresent()) {
            availableColors.add(nation.get().getTeamColor());
            nations.remove(nation.get());
            if (nations.size() == 0) {
                controller.terminateLobby(this, null);
            }
            return true;
        }
        return false;
    }

    public void readyCheck(String username, boolean ready, Map<String, Integer> stats) {
        nations.stream()
                .filter(nation -> nation.getUsername().equals(username))
                .findFirst().ifPresent(nation -> { nation.setReady(ready);
                nation.setPersonWithStats(stats);
                });
    }

    public List<Map<String, Object>> checkPlayerState() {
        List<Map<String, Object>> data = new ArrayList<>();
        nations.forEach(nation -> {
            Map<String, Object> map = new HashMap<>();
            map.put("isReady", nation.isReady());
            map.put("name", nation.getUsername());
            data.add(map);
        });
        return data;
    }

    public void sendUpdateToMap(){
        gameField.updateMap(cellsToUpdate);
        lastUpdate.clear();
        lastUpdate.putAll(cellsToUpdate);
        cellsToUpdate.clear();
    }


    public synchronized void addUpdatedState(Map<Coordinates, Person> nationUpdate) {
        // Checks for overlapping keys with non-null values
        if (!Collections.disjoint(cellsToUpdate.keySet().stream().filter(Objects::nonNull)
                .collect(Collectors.toSet()), nationUpdate.keySet())) {
            Set<Coordinates> overlappingKeys = new HashSet<>(cellsToUpdate.keySet());
            overlappingKeys.retainAll(nationUpdate.keySet());
            // Removes person from nation list if it's overwritten by another nation
            // TODO: probably can think of a better and faster way to do this
            for (Coordinates key : overlappingKeys) {
                cellsToUpdate.get(key).getNation().removePerson(cellsToUpdate.get(key));
            }
        }
        cellsToUpdate.putAll(nationUpdate);
    }

    public Map<Coordinates, Person> getLastUpdate() {
        return lastUpdate;
    }

    public void endTurn() {
        waiting++;
        if (waiting >= 4) {
            sendUpdateToMap();
            if (checkWinner().isPresent()) {
                controller.terminateLobby(this, checkWinner().get().getUsername());
                return;
            }
            waiting = 0;
            numOfTurns++;
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
