package com.ttu.tarkvaratehnika.empires.gameofempires.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.util.HashSet;
import java.util.OptionalDouble;
import java.util.Set;

public class InGameStats {

    public static JsonArray getNationStatistics(Set<Nation> nations) {
        JsonArray array = new JsonArray();
        for (Nation nation : nations) {
            JsonObject object = new JsonObject();
            object.addProperty("username", nation.getUsername());
            object.addProperty("color", nation.getTeamColor());
            object.addProperty("nationSize", nation.getNumOfPeople());

            Set<Person> people = new HashSet<>(nation.getPeople());
            OptionalDouble vitality = people.stream().mapToInt(Person::getVitality).average();
            OptionalDouble strength = people.stream().mapToInt(Person::getStrength).average();
            OptionalDouble dexterity = people.stream().mapToInt(Person::getDexterity).average();
            OptionalDouble intelligence = people.stream().mapToInt(Person::getIntelligence).average();
            OptionalDouble reproduction = people.stream().mapToInt(Person::getGrowthRate).average();
            OptionalDouble luck = people.stream().mapToInt(Person::getLuck).average();

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
