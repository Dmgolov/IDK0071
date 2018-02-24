package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.util.Map;
import java.util.Random;

//TODO: improve methods from interface.
public class Person implements BasicPerson {

    private Nation nation;
    private GameField field;

    private int positionX, positionY;

    private int vitality;
    private int strength;
    private int dexterity;
    private int intelligence;
    private int growthRate;
    private int luck;

    private InGameObject effectedBy;

    private Random random = new Random();

    public Person(Nation nation, GameField field) {
        this.nation = nation;
        this.field = field;
        nation.addPerson(this);
    }

    public Person(Nation nation, GameField field, Map<String, Integer> stats) {
        this(nation, field);
        vitality = stats.get("Vitality");
        strength = stats.get("Strength");
        dexterity = stats.get("Dexterity");
        intelligence = stats.get("Intelligence");
        growthRate = stats.get("Reproduction");
        luck = stats.get("Luck");
    }

    @Override
    public void move() {

    }

    @Override
    public boolean reproduce() {
        int result = random.nextInt(PersonValues.REPRODUCTION_CHANCE);
        return growthRate >= result;
    }

    @Override
    public boolean resistDisease() {
        int result = random.nextInt(PersonValues.DISEASE_CHANCE);
        return luck >= result;
    }

    @Override
    public Person fight(Person another) {
        return strength > another.getStrength() ? this : another;
    }

    public void addEffect(InGameObject inGameObject) {
        this.effectedBy = inGameObject;
    }

    private void removeEffect() {
        effectedBy = null;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public int getVitality() {
        return vitality;
    }

    public void setVitality(int vitality) {
        this.vitality = vitality;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(int growthRate) {
        this.growthRate = growthRate;
    }

    public InGameObject getEffectedBy() {
        return effectedBy;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }
}
