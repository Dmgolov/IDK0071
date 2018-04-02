package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

// TODO: find a way to set different landscapes to different maps
public enum GameMap {
    BASIC_MAP("basic", 100, 100),
    LARGE_MAP("large", 50, 50),
    EARTH("earth", 300, 200);

    private String mapName;
    private int mapWidth;
    private int mapHeight;

    GameMap(final String mapName, final int mapWidth, final int mapHeight) {
        this.mapName = mapName;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public String getMapName() {
        return mapName;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }
}
