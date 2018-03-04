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

    private GameField gameField = new GameField();

    private String lobbyName;
    private String lobbyPass;
    private Set<Nation> nations = new HashSet<>();
    private Map<Coordinates, Person> cellsToUpdate = new HashMap<>();
    private List<String> availableColors = new ArrayList<>(Arrays.asList(generateNationColor(), generateNationColor(), generateNationColor(), generateNationColor()));
    private volatile int waiting = 0;
    private boolean singleMode;

    public GameLobby(LobbyController controller) {
        this.controller = controller;
        lobbyId = ++id;
    }

    private void startSession() {
        nations.stream().filter(nation -> !nation.hasSelectedPersonType()).forEach(Nation::setDefaultPerson);
        if (!gameField.isMapSet()) gameField.setGameMap(SessionSettings.DEFAULT_MAP);
        gameField.loadField();
        nations.forEach(nation -> nation.getPerson().setStartingLocation());
        try {
            Thread.sleep(SessionSettings.START_DELAY);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        nations.forEach(nation -> new Thread(nation).start());
    }

    private Optional<Nation> checkWinner() {
        long active = nations.stream().filter(Nation::isActive).count();
        if (active == 1) {
            return nations.stream().filter(Nation::isActive).findFirst();
        } else if (active == 0) {
            return Optional.of(new Nation("none", null, this));
        } else if (numOfTurns >= SessionSettings.MAX_TURNS) {
            return nations.stream().max(Comparator.comparingInt(Nation::getNumOfPeople));
        }
        return Optional.empty();
    }

    public void changeToSinglePlayer() {
        singleMode = true;
        for (int i = 1; hasFreeSpaces(); i++) {
            enterSession("bot" + i).ifPresent(nation -> nation.setReady(true));
        }
    }

    public Optional<Nation> enterSession(String username) {
        if (hasFreeSpaces()) {
            Nation nation = new Nation(username, availableColors.get(0), this);
            System.out.println(availableColors);
            availableColors.remove(0);
            nations.add(nation);
            return Optional.of(nation);
        }
        return Optional.empty();
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
                .findFirst().ifPresent(nation -> {
            nation.setReady(ready);
            nation.setPersonWithStats(stats);
        });
        if (nations.stream().filter(Nation::isReady).count() == SessionSettings.DEFAULT_MAX_USERS) startSession();
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

    private void sendUpdateToMap() {
        gameField.updateMap(cellsToUpdate, numOfTurns);
        cellsToUpdate = new HashMap<>();
    }

    private Set<Coordinates> findKeysWithNonNullValues(Map<Coordinates, Person> peopleLocation) {
        return peopleLocation.keySet().stream().filter(key -> peopleLocation.get(key) != null).collect(Collectors.toSet());
    }

    public synchronized void addUpdatedState(Map<Coordinates, Person> nationUpdate) {
        // Checks for overlapping keys with non-null values
        //System.out.println("Checking multiples");
        Set<Coordinates> overlappingKeys = findKeysWithNonNullValues(cellsToUpdate);
        if (overlappingKeys.retainAll(findKeysWithNonNullValues(nationUpdate))) {
            // Compares 2 people and removes one of them
            // TODO: check, if works fast enough
            //System.out.println("iterating keys");
            for (Coordinates key : overlappingKeys) {
                Person person1 = cellsToUpdate.get(key);
                Person person2 = nationUpdate.get(key);
                //System.out.println("Capturing cell");
                if (person2.captureCell(person1)) {
                    person1.getNation().removePerson(person1);
                } else {
                    person2.getNation().removePerson(person2);
                    nationUpdate.remove(key);
                }
            }
        }
        //System.out.println("adding cells");
        cellsToUpdate.putAll(nationUpdate);
    }

    public synchronized void endTurn() {
        waiting++;
        //System.out.println("Added wait: " + waiting);
        if (allNationsWaiting()) {
            System.out.println("Updating game map");
            sendUpdateToMap();
        }
    }

    public void startNewTurn() {
        if (allNationsWaiting()) {
            waiting = 0;
            numOfTurns++;
            //System.out.println("Turn nr: " + numOfTurns);
            //System.out.println("Checking winner");
            if (checkWinner().isPresent()) {
                System.out.println("Terminating lobby");
                controller.terminateLobby(this, checkWinner().get().getUsername());
                System.out.println("Winner: " + checkWinner().get().getUsername());
                nations.forEach(nation -> System.out.println(nation.getUsername() + " has " + nation.getNumOfPeople() + " people"));
                nations.forEach(nation -> nation.getPeople().clear());
                synchronized (this) {
                    notifyAll();
                }
                return;
            }
            //System.out.println("Waking nations");
            synchronized (this) {
                notifyAll();
            }
        } else {
            //System.out.println("more nations to wait");
        }
    }

    public boolean allNationsWaiting() {
        return waiting >= nations.stream().filter(Nation::isActive).count();
    }

    public boolean hasFreeSpaces() {
        return nations.size() < SessionSettings.DEFAULT_MAX_USERS;
    }

    public Set<Nation> getNations() {
        return nations;
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

    public boolean isSingleMode() {
        return singleMode;
    }

    public void setSingleMode(boolean singleMode) {
        this.singleMode = singleMode;
    }

    private static String generateNationColor() {
        String[] hexElements = "0123456789abcdef".split("");
        StringBuilder nationColor = new StringBuilder("#");
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            nationColor.append(hexElements[random.nextInt(15)]);
        }
        if (Objects.equals(nationColor.toString(), "#00ff00")){
            generateNationColor();
        } else {
            return nationColor.toString();
        }
        return nationColor.toString();
    }
}
