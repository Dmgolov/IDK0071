package com.ttu.tarkvaratehnika.empires.gameofempires.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.util.OptionalDouble;
import java.util.Set;

public class InGameStats {

    public static JsonArray getNationStatistics(Set<Nation> nations) {
        JsonArray array = new JsonArray();
        for (Nation nation : nations) {
            JsonObject object = new JsonObject();
            object.addProperty("username", nation.getUsername());
            object.addProperty("color", nation.getTeamColor());
            OptionalDouble vitality = nation.getPeople().stream().mapToInt(Person::getVitality).average();
            OptionalDouble strength = nation.getPeople().stream().mapToInt(Person::getStrength).average();
            OptionalDouble dexterity = nation.getPeople().stream().mapToInt(Person::getDexterity).average();
            OptionalDouble intelligence = nation.getPeople().stream().mapToInt(Person::getIntelligence).average();
            OptionalDouble reproduction = nation.getPeople().stream().mapToInt(Person::getGrowthRate).average();
            OptionalDouble luck = nation.getPeople().stream().mapToInt(Person::getLuck).average();
            object.addProperty("Vitality", vitality.isPresent() ? vitality.getAsDouble() : 0);
            object.addProperty("Strength", strength.isPresent() ? strength.getAsDouble() : 0);
            object.addProperty("Dexterity", dexterity.isPresent() ? dexterity.getAsDouble() : 0);
            object.addProperty("Intelligence", intelligence.isPresent() ? intelligence.getAsDouble() : 0);
            object.addProperty("Reproduction", reproduction.isPresent() ? reproduction.getAsDouble() : 0);
            object.addProperty("Luck", luck.isPresent() ? luck.getAsDouble() : 0);
            array.add(object);
        }
        return array;
    }
}
