package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.controller.LobbyController;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.io.IOException;
import java.util.*;

public class GameLobby {

    private static long id = 0;
    private int numOfTurns = 0;

    private LobbyController controller;
    private final long lobbyId;

    private GameField gameField = new GameField();

    private String lobbyName;
    private Set<Nation> nations = new HashSet<>();
    private final List<String> receivedUpdate = new ArrayList<>();
    private List<String> usedColors = new ArrayList<>(5);
    private List<String> availableColors = new ArrayList<>(Arrays.asList(generateNationColor(), generateNationColor(), generateNationColor(), generateNationColor()));
    private volatile int waiting = 0;
    private int botsPlayersCount = 0;
    private boolean singleMode;
    private boolean hasStarted = false;

    public GameLobby(LobbyController controller) {
        this.controller = controller;
        lobbyId = ++id;
    }

    private void startSession() throws IOException {
        nations.stream().filter(nation -> !nation.hasSelectedPersonType()).forEach(Nation::setDefaultPerson);
        if (!gameField.isMapSet()) gameField.setGameMap(SessionSettings.DEFAULT_MAP);
        gameField.loadField();
        nations.forEach(Nation::setStartingLocation);
        try {
            Thread.sleep(SessionSettings.START_DELAY);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        hasStarted = true;
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
        int i;
        for (i = 0; hasFreeSpaces(); i++) {
            enterSession("bot" + i).ifPresent(nation -> nation.setReady(true));
        }
        botsPlayersCount = i;
    }

    public Optional<Nation> enterSession(String username) {
        if (hasFreeSpaces() && !hasStarted) {
            Nation nation = new Nation(username, availableColors.get(0), this);
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
                controller.terminateLobby(this, null, null);
            }
            return true;
        }
        return false;
    }

    public void readyCheck(String username, boolean ready, Map<String, Integer> stats) throws IOException {
        nations.stream()
                .filter(nation -> nation.getUsername().equals(username))
                .findFirst().ifPresent(nation -> {
            nation.setReady(ready);
            nation.setPersonWithStats(stats);
        });
        if (nations.stream().filter(Nation::isReady).count() == nations.size()) startSession();
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

    public Optional<JsonObject> sendUpdateToUser(String username, int turnNr) {
        if (!receivedUpdate.contains(username) && allNationsWaiting() && gameField.hasLastUpdate()) {
            System.out.println(username + " received update");
            receivedUpdate.add(username);
            return Optional.of(gameField.getLastMapUpdate(turnNr));
        }
        return Optional.empty();
    }

    public void checkIfEveryoneReceivedUpdate() {
        if (receivedUpdate.size() >= nations.size() - botsPlayersCount && allNationsWaiting()) {
            gameField.clearLastUpdate();
            receivedUpdate.clear();
            startNewTurn();
        }
    }

    public synchronized void endTurn() {
        waiting++;
        if (allNationsWaiting()) {
            System.out.println("Updating game map");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameField.updateMap();
        }
    }

    private void startNewTurn() {
        if (allNationsWaiting()) {
            waiting = 0;
            numOfTurns++;
            if (checkWinner().isPresent()) {
                Nation winner = checkWinner().get();
                controller.terminateLobby(this, winner.getUsername(), winner.getTeamColor());
                nations.forEach(nation -> nation.getPeople().clear());
                synchronized (this) {
                    notifyAll();
                }
                return;
            }
            System.out.println("Waking nations");
            synchronized (this) {
                notifyAll();
            }
        }
    }

    private boolean allNationsWaiting() {
        return waiting >= nations.stream().filter(Nation::isActive).count();
    }

    private boolean hasFreeSpaces() {
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

    public long getLobbyId() {
        return lobbyId;
    }

    public boolean isSingleMode() {
        return singleMode;
    }

    public void setSingleMode(boolean singleMode) {
        this.singleMode = singleMode;
    }

    private String generateNationColor() {
        String nationColor = "";
        Random random = new Random();
        nationColor = SessionSettings.NATION_COLORS[random.nextInt(4)];
        if (!usedColors.contains(nationColor)){
            usedColors.add(nationColor);
            return nationColor;
        } else {
            return generateNationColor();
        }
    }

    public List<Map<String, String>> getPlayerColors() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Nation nation : nations) {
            Map<String, String> playerData = new HashMap<>();
            playerData.put("name", nation.getUsername());
            playerData.put("color", nation.getTeamColor());
            data.add(playerData);
        }
        return data;
    }
}
