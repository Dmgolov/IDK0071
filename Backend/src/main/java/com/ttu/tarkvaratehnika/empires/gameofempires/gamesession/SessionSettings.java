package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;

public class SessionSettings {

    public static final int DEFAULT_MAX_USERS = 4;
    public static final int NATION_POINTS = 20;
    public static final int MAX_TURNS = 5_000;
    public static final int START_DELAY = 2_000;

    public static final String SINGLE_PLAYER = "single";
    public static final String MULTI_PLAYER = "multi";

    public static final GameMap DEFAULT_MAP = GameMap.LARGE_MAP;
    public static final String[] NATION_COLORS = new String[]{"#ff0000", "#ffff00", "#ff00ff", "#00ff00",
            "#00ffff", "#ff6666"};
}
