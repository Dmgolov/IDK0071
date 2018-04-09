package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Person implements InGameObject {

    private Nation nation;
    private GameField field;

    private int positionX, positionY;

    private int vitality;
    private int strength;
    private int dexterity;
    private int intelligence;
    private int growthRate;
    private int luck;

    private Terrain terrain;

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

    public void act() {
        if (!resistDisease()) {
            die();
            return;
        }
        mutate(PersonValues.MUTATION_CHANCE);
        List<Coordinates> neighbourCells = getFreeNeighbourCells();
        if (hasFreeNeighbourCells(neighbourCells)) {
            Coordinates newLocation = neighbourCells.get(random.nextInt(neighbourCells.size()));
            if (canReproduce()) {
                reproduce(newLocation.getX(), newLocation.getY());
            } else {
                move(newLocation.getX(), newLocation.getY());
            }
        } else {
            die();
        }
    }

    private void move(int newX, int newY) {
        field.movePerson(this, newX, newY, positionX, positionY);
    }

    private void reproduce(int x, int y) {
        mutate(PersonValues.MUTATION_CHANCE + PersonValues.REPRODUCTION_MUTATION_RESIST);
        field.addPersonToCell(new Person(this), x, y);
    }

    void die() {
        field.removePersonFromCell(positionX, positionY);
    }

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
                if (object instanceof Person) {
                    if (((Person) object).isEnemy(this)) {
                        freeCells.add(new Coordinates(newX, newY));
                    }
                } else if (object instanceof Terrain) {
                    if (((Terrain) object).isPassable()) {
                        freeCells.add(new Coordinates(newX, newY));
                    }
                }
            }
        }
        return freeCells;
    }

    private boolean hasFreeNeighbourCells(List<Coordinates> neighbourCells) {
        return !neighbourCells.isEmpty();
    }

    private boolean isEnemy(Person another) {
        return !nation.equals(another.getNation());
    }

    void mutate(int chance) {
        int result = random.nextInt(chance);
        if (result == 0) {
            int statChange = ThreadLocalRandom.current().nextInt(PersonValues.MIN_STAT_CHANGE, PersonValues.MAX_STAT_CHANGE + 1);
            changeRandomStat(statChange);
        }
    }

    void changeRandomStat(int change) {
        int statNum = random.nextInt(PersonValues.DEFAULT_STATS.size());
        switch (statNum) {
            case 0:
                vitality += change;
                break;
            case 1:
                strength += change;
                break;
            case 2:
                dexterity += change;
                break;
            case 3:
                intelligence += change;
                break;
            case 4:
                luck += change;
                break;
            case 5:
                growthRate += change;
                break;
        }
    }

    public boolean canReproduce() {
        return random.nextInt(Math.max(PersonValues.REPRODUCTION_CHANCE - growthRate, 1)) == 0;
    }

    public boolean resistDisease() {
        return random.nextInt(Math.max(PersonValues.DISEASE_CHANCE - luck, 1)) == 0;
    }

    public boolean captureCell(Person another) {
        return this.getAttackStrength() > another.getDefence();
    }

    private double getAttackStrength() {
        double attack = strength;
        int criticalHitResult = random.nextInt(Math.max(PersonValues.CRIT_CHANCE - dexterity, 1));
        if (criticalHitResult == 0) attack *= PersonValues.CRIT_MULTIPLIER;
        return attack * terrain.getAttackMultiplier();
    }

    private double getDefence() {
        double defence = vitality;
        double intelligenceModifier = intelligence / 10.0;
        return defence * (terrain.getDefenceMultiplier() + intelligenceModifier);
    }

    public void addEffect(Terrain terrain) {
        this.terrain = terrain;
    }

    public Terrain removeEffect() {
        Terrain effect = Terrain.findByColor(terrain.getColorHex());
        this.terrain = null;
        return effect;
    }

    public void removeFromNation() {
        nation.removePerson(this);
    }

    public void addToNation() {
        nation.addPerson(this);
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

    public Terrain getEffectedBy() {
        return terrain;
    }

    public void setRandom(Random random) {
        this.random= random;
    }

    int getVitality() {
        return vitality;
    }

    @Override
    public String toString() {
        return "Person of " + nation.getUsername();
    }

    @Override
    public String getColorHex() {
        return nation.getTeamColor();
    }
}
