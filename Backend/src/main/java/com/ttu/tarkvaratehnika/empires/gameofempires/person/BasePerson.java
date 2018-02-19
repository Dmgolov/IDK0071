package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;

//TODO: implement methods from interface. Create persons based on this class
//Abstract base for another types of person.
abstract class BasePerson implements Person {

    private int vitality;
    private int speed;
    private int strength;
    private int dexterity;
    private int intelligence;
    private int growthRate;
    private int luck;

    private InGameObject effectedBy;

    public void addEffect(InGameObject inGameObject) {
        this.effectedBy = inGameObject;
    }

    private void removeEffect() {
        effectedBy = null;
    }

    public int getVitality() {
        return vitality;
    }

    public void setVitality(int vitality) {
        this.vitality = vitality;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
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
