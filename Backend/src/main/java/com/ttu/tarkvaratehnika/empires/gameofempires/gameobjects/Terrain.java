package com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects;

//Base for terrain types (eg. water, swamp, etc.), which can have effect on people
public enum Terrain implements InGameObject {
    PLAINS("#8DB360", true, 1, 1),
    DESERT("#FA9418", true, 0.9, 1.1),
    FOREST("#056618", true, 1.1, 0.9),
    OCEAN("#000070", false, 1, 1),
    RIVER("#0000FF", true, 0.95, 0.9);

    private final String colorHex;
    private final boolean isPassable;
    private final double attackMultiplier;
    private final double defenceMultiplier;

    Terrain(String colorHex, boolean isPassable, double attackMultiplier, double defenceMultiplier){
        this.colorHex = colorHex;
        this.isPassable = isPassable;
        this.attackMultiplier = attackMultiplier;
        this.defenceMultiplier = defenceMultiplier;
    }

    public static Terrain findByColor(String colorHex) {
        for (Terrain terrain : values()) {
            if (terrain.getColorHex().equals(colorHex)) return terrain;
        }
        return Terrain.PLAINS;
    }

    public boolean isPassable() {
        return this.isPassable;
    }

    @Override
    public String getColorHex() {
        return this.colorHex;
    }

    public double getAttackMultiplier() {
        return attackMultiplier;
    }

    public double getDefenceMultiplier() {
        return defenceMultiplier;
    }
}
