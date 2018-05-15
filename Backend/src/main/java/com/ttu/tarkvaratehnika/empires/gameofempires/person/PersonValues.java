package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import java.util.HashMap;
import java.util.Map;

public class PersonValues {

    public static final int REPRODUCTION_CHANCE = 30;
    public static final int DISEASE_CHANCE = 7;
    public static final int MUTATION_CHANCE = 9;
    public static final int REPRODUCTION_MUTATION_RESIST = 5;
    public static final int MIN_STAT_CHANGE = -2;
    public static final int MAX_STAT_CHANGE = 2;
    public static final int CRIT_CHANCE = 20;
    public static final double CRIT_MULTIPLIER = 2;
    public static final int IMMORTAL_TILL_TURN = 75;

    public static final int DEFAULT_VITALITY = 3;
    public static final int DEFAULT_STRENGTH = 4;
    public static final int DEFAULT_DEXTERITY = 3;
    public static final int DEFAULT_INTELLIGENCE = 3;
    public static final int DEFAULT_REPRODUCTION = 4;
    public static final int DEFAULT_LUCK = 3;

    public static final String VITALITY = "Vitality";
    public static final String STRENGTH = "Strength";
    public static final String DEXTERITY = "Dexterity";
    public static final String INTELLIGENCE = "Intelligence";
    public static final String REPRODUCTION = "Reproduction";
    public static final String LUCK = "Luck";

    public static final Map<String, Integer> DEFAULT_STATS = new HashMap<>();
    static {
        DEFAULT_STATS.put(VITALITY, DEFAULT_VITALITY);
        DEFAULT_STATS.put(STRENGTH, DEFAULT_STRENGTH);
        DEFAULT_STATS.put(DEXTERITY, DEFAULT_DEXTERITY);
        DEFAULT_STATS.put(INTELLIGENCE, DEFAULT_INTELLIGENCE);
        DEFAULT_STATS.put(REPRODUCTION, DEFAULT_REPRODUCTION);
        DEFAULT_STATS.put(LUCK, DEFAULT_LUCK);
    }
}
