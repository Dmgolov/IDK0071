package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

public class Properties {

    private long lobbyId;
    private int nationPoints;
    private int iterationsNumber;
    private int maxPlayersNumber;
    private String mapName;

    public Properties() {
        lobbyId = 0;
        nationPoints = SessionSettings.NATION_POINTS;
        iterationsNumber = SessionSettings.DEFAULT_NUM_OF_TURNS;
        maxPlayersNumber = SessionSettings.DEFAULT_NUM_OF_PLAYERS;
        mapName = SessionSettings.DEFAULT_MAP;
    }

    public Properties(long lobbyId, int nationPoints, int iterationsNumber, int maxPlayersNumber, String mapName) {
        this.lobbyId = lobbyId;
        this.nationPoints = nationPoints;
        this.iterationsNumber = iterationsNumber;
        this.maxPlayersNumber = maxPlayersNumber;
        this.mapName = mapName;
    }

    public int getNationPoints() {
        return nationPoints;
    }

    public void setNationPoints(int nationPoints) {
        this.nationPoints = nationPoints;
    }

    public int getIterationsNumber() {
        return iterationsNumber;
    }

    public void setIterationsNumber(int iterationsNumber) {
        this.iterationsNumber = iterationsNumber;
    }

    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }

    public void setMaxPlayersNumber(int maxPlayersNumber) {
        this.maxPlayersNumber = maxPlayersNumber;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(long lobbyId) {
        this.lobbyId = lobbyId;
    }
}
