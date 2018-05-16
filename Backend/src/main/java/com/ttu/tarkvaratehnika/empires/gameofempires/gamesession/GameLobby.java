package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.SessionService;
import com.ttu.tarkvaratehnika.empires.gameofempires.stats.InGameStats;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class GameLobby {

    private int numOfTurns = 0;
    private LocalDateTime startTime;

    private SessionService sessionService;
    private final long lobbyId;

    private Properties properties;

    private GameField gameField = new GameField(SessionSettings.DEFAULT_MAP);

    private Set<Nation> nations = new HashSet<>();
    private final List<String> receivedUpdate = new ArrayList<>();
    private List<String> availableColors;
    private volatile int waiting = 0;
    private int botsPlayersCount = 0;
    private boolean singleMode;
    private boolean hasStarted = false;
    private int startDelay = SessionSettings.START_DELAY;

    public GameLobby(SessionService sessionService, long id) {
        this.sessionService = sessionService;
        properties = new Properties();
        availableColors = new ArrayList<>(Arrays.asList(SessionSettings.NATION_COLORS));
        lobbyId = id;
    }

    void startSession() throws IOException {
        nations.stream().filter(nation -> !nation.hasSelectedPersonType()).forEach(Nation::setDefaultPerson);
        nations.forEach(Nation::setStartingLocation);
        try {
            Thread.sleep(startDelay);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        hasStarted = true;
        startTime = LocalDateTime.now();
        nations.forEach(nation -> new Thread(nation).start());
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
            if (nations.size() - botsPlayersCount == 0) {
                sessionService.terminateLobbyWithoutSave(this);
            }
            return true;
        }
        return false;
    }

    public void changeToSinglePlayer() {
        singleMode = true;
        int i;
        for (i = 0; hasFreeSpaces(); i++) {
            Optional<Nation> bot = enterSession("Bot " + (i + 1));
            bot.ifPresent(nation -> nation.setReady(true));
        }
        botsPlayersCount = i;
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

    public Optional<JsonObject> sendUpdateToUser(String username, int turnNr) {
        if (!receivedUpdate.contains(username) && allNationsWaiting() && gameField.hasLastUpdate()) {
            receivedUpdate.add(username);
            Optional<JsonObject> update = Optional.of(gameField.getLastMapUpdate(turnNr));
            if (update.isPresent()) {
                JsonObject jsonObject = update.get();
                jsonObject.add("stats", InGameStats.getNationStatistics(nations));
                return update;
            }
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
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameField.updateMap();
        }
    }

    void startNewTurn() {
        if (allNationsWaiting()) {
            waiting = 0;
            numOfTurns++;
            if (checkWinner().isPresent()) {
                Nation winner = checkWinner().get();
                sessionService.terminateLobby(this, winner.getUsername(), winner.getTeamColor());
                nations.forEach(nation -> nation.getPeople().clear());
                synchronized (this) {
                    notifyAll();
                }
                return;
            }
            synchronized (this) {
                notifyAll();
            }
        }
    }

    Optional<Nation> checkWinner() {
        long active = nations.stream().filter(Nation::isActive).count();
        if (active == 1) {
            return nations.stream().filter(Nation::isActive).findFirst();
        } else if (active == 0) {
            return Optional.of(new Nation("none", "transparent", this));
        } else if (numOfTurns >= properties.getIterationsNumber()) {
            return nations.stream().max(Comparator.comparingInt(Nation::getNumOfPeople));
        }
        return Optional.empty();
    }

    boolean allNationsWaiting() {
        return waiting >= nations.stream().filter(Nation::isActive).count();
    }

    private boolean hasFreeSpaces() {
        return nations.size() < properties.getMaxPlayersNumber();
    }

    public GameField getGameField() {
        return gameField;
    }

    public void setGameField(GameField gameField){
        this.gameField = gameField;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public long getLobbyId() {
        return lobbyId;
    }

    public int getNumOfTurns() {
        return numOfTurns;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    Set<Nation> getNations() {
        return nations;
    }

    void setNations(Set<Nation> nations) {
        this.nations = nations;
    }

    void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public boolean isSingleMode() {
        return singleMode;
    }

    public void setSingleMode(boolean singleMode) {
        this.singleMode = singleMode;
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

    void setStartDelay(int timeInMilliseconds) {
        startDelay = timeInMilliseconds;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
        gameField.setGameMap(properties.getMapName());
        try {
            gameField.loadField();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (singleMode) {
            changeToSinglePlayer();
        }
    }
}
