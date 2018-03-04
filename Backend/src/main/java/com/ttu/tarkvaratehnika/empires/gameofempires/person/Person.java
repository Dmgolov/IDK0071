package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.util.*;

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

    private Person(Nation nation, GameField field) {
        this.nation = nation;
        this.field = field;
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

    //Copy-constructor
    public Person(Person another) {
        this(another.nation, another.field);
        vitality = another.vitality;
        strength = another.strength;
        dexterity = another.dexterity;
        intelligence = another.intelligence;
        growthRate = another.growthRate;
        luck = another.luck;
    }

    @Override
    public void act() {
        if (!resistDisease()) { // if person dies, remove
            nation.removePersonFromCoordinates(positionX, positionY);
            return;
        }
        List<Coordinates> neighbourCells = getFreeNeighbourCells();
        if (hasFreeNeighbourCells(neighbourCells)) { //check near cells.
            Coordinates newLocation = neighbourCells.get(random.nextInt(neighbourCells.size()));
            if (reproduce()) { // if can reproduce, add new person to new cell
                nation.setPersonToCoordinates(newLocation.getX(), newLocation.getY());
            } else { // if cannot reproduce, move to new cell
                nation.movePersonToCoordinates(this, newLocation.getX(), newLocation.getY(), positionX, positionY);
            }
        }
    }

    @Override
    public List<Coordinates> getFreeNeighbourCells() {
        List<Coordinates> freeCells = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                int newX = Math.floorMod(positionX + x, field.getMapWidth());
                int newY = Math.floorMod(positionY + y, field.getMapHeight());
                InGameObject object = field.getObjectInCell(newX, newY);
                if (!(object instanceof Person)
                        && !nation.getUpdatedPositions().containsKey(new Coordinates(newX, newY))) {
                    freeCells.add(new Coordinates(newX, newY));
                }
            }
        }
        return freeCells;
    }

    private boolean hasFreeNeighbourCells(List<Coordinates> neighbourCells) {
        return !neighbourCells.isEmpty();
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

    //TODO: implement better way to compare stats
    @Override
    public boolean captureCell(Person another) {
        return strength > another.getStrength();
    }

    public void setStartingLocation() {
        positionX = random.nextInt(field.getMapWidth());
        positionY = random.nextInt(field.getMapHeight());
        while (field.getObjectInCell(positionX, positionY) instanceof Person) {
            positionX = random.nextInt(field.getMapWidth());
            positionY = random.nextInt(field.getMapHeight());
        }
        nation.addPerson(this);
    }

    public void addEffect(InGameObject inGameObject) {
        this.effectedBy = inGameObject;
    }

    public InGameObject removeEffect() {
        InGameObject effect = effectedBy;
        effectedBy = null;
        return effect;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getPositionY() {
        return positionY;
    }

    public Nation getNation() {
        return nation;
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

    @Override
    public String toString() {
        return "Person of " + nation.getUsername();
    }
}
