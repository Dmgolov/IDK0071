package com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects;

//Base for terrain types (eg. water, swamp, etc.), which can have effect on people
public enum Terrain implements InGameObject {
    PLAINS("#8DB360", true),
    DESERT("#FA9418", true),
    FOREST("#056618", true),
    OCEAN("#000070", false),
    RIVER("#0000FF", true);

    private final String colorHex;
    private final boolean isPassable;

    Terrain(String colorHex, boolean isPassable){
        this.colorHex = colorHex;
        this.isPassable = isPassable;
    };

    public boolean isPassable() {
        return this.isPassable;
    }

    @Override
    public String getColorHex() {
        return this.colorHex;
    }
}
