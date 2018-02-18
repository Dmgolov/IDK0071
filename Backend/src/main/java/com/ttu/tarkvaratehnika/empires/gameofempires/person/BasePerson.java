package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;

//TODO: implement methods from interface. Create persons based on this class
//Abstract base for another types of person.
abstract class BasePerson implements Person {

    private int health;
    private int speed;
    private int strength;
    private int agility;
    private int intelligence;
    private int reproduction;

    private InGameObject effectedBy;

    public void addEffect(InGameObject inGameObject) {
        this.effectedBy = inGameObject;
    }

    private void removeEffect() {
        effectedBy = null;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getReproduction() {
        return reproduction;
    }

    public void setReproduction(int reproduction) {
        this.reproduction = reproduction;
    }

    public InGameObject getEffectedBy() {
        return effectedBy;
    }
}
