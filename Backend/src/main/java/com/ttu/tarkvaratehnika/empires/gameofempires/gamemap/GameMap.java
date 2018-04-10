package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

// TODO: find a way to set different landscapes to different maps
public class GameMap {

    private String imageName;

    GameMap(String imageName) {
        this.imageName = imageName;
    }

    public String getMapName() {
        return imageName;
    }
}
