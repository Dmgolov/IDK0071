package com.ttu.tarkvaratehnika.empires.gameofempires.gamesession;


public class SessionSettings {

    public static final String[] NATION_COLORS = new String[]{"#B4110F", "#A40FA6", "#663300", "#7587db", "#24253d",
            "#c7002d", "#82638c", "#134444", "#7c007d", "#c70000", "#f5a30a", "#c700b1", "#581e21", "#006461",
            "#b0aeab", "#b0ae28"};

    public static final int DEFAULT_MAX_USERS = NATION_COLORS.length;
    public static final int DEFAULT_NUM_OF_PLAYERS = 4;
    public static final int NATION_POINTS = 20;
    public static final int MAX_TURNS = 5000;
    public static final int DEFAULT_NUM_OF_TURNS = 600;
    public static final int START_DELAY = 2_000;

    public static final String DEFAULT_MAP = "gameMap5.png";
    public static final String SINGLE_PLAYER = "single";
    public static final String MULTI_PLAYER = "multi";
}
