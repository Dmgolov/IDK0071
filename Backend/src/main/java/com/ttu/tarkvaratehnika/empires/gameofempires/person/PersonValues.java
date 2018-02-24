package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import java.util.HashMap;
import java.util.Map;

public class PersonValues {

    public static final int MAX_STAT = 20;
    public static final int REPRODUCTION_CHANCE = 20;
    public static final int DISEASE_CHANCE = 26;

    public static final int DEFAULT_VITALITY = 3;
    public static final int DEFAULT_STRENGTH = 4;
    public static final int DEFAULT_DEXTERITY = 3;
    public static final int DEFAULT_INTELLIGENCE = 3;
    public static final int DEFAULT_REPRODUCTION = 4;
    public static final int DEFAULT_LUCK = 3;

    public static final Map<String, Integer> DEFAULT_STATS = new HashMap<>();
    static {
        DEFAULT_STATS.put("Vitality", DEFAULT_VITALITY);
        DEFAULT_STATS.put("Strength", DEFAULT_STRENGTH);
        DEFAULT_STATS.put("Dexterity", DEFAULT_DEXTERITY);
        DEFAULT_STATS.put("Intelligence", DEFAULT_INTELLIGENCE);
        DEFAULT_STATS.put("Reproduction", DEFAULT_REPRODUCTION);
        DEFAULT_STATS.put("Luck", DEFAULT_LUCK);
    }
}
