package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    private List<Coordinates> deadCells = new ArrayList<>();

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
            field.removePersonFromCell(positionX, positionY);
            return;
        }
        mutate(PersonValues.MUTATION_CHANCE);
        List<Coordinates> neighbourCells = getFreeNeighbourCells();
        if (hasFreeNeighbourCells(neighbourCells)) { //check near cells.
            Coordinates newLocation = neighbourCells.get(random.nextInt(neighbourCells.size()));
            if (reproduce()) { // if can reproduce, add new person to new cell
                mutate(PersonValues.MUTATION_CHANCE + PersonValues.REPRODUCTION_MUTATION_RESIST);
                field.addPersonToCell(new Person(this), newLocation.getX(), newLocation.getY());
            } else { // if cannot reproduce, move to new cell
                field.movePerson(this, newLocation.getX(), newLocation.getY(), positionX, positionY);
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

    private void mutate(int chance) {
        int result = random.nextInt(chance);
        if (result == 0) {
            int statChange = ThreadLocalRandom.current().nextInt(PersonValues.MIN_STAT_CHANGE, PersonValues.MAX_STAT_CHANGE + 1);
            changeRandomStat(statChange);
        }
    }

    private void changeRandomStat(int change) {
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

    @Override
    public boolean reproduce() {
        return random.nextInt(Math.max(PersonValues.REPRODUCTION_CHANCE - growthRate, 1)) == 0;
    }

    @Override
    public boolean resistDisease() {
        return random.nextInt(Math.max(PersonValues.DISEASE_CHANCE - luck, 1)) == 0;
    }

    //TODO: implement better way to compare stats
    @Override
    public boolean captureCell(Person another) {
        return this.getAttackStrength() > another.getAttackStrength();
    }

    private double getAttackStrength() {
        double attack = strength;
        int criticalHitResult = random.nextInt(Math.max(PersonValues.CRIT_CHANCE - dexterity, 1));
        if (criticalHitResult == 0) attack *= PersonValues.CRIT_MULTIPLIER;
        return attack;
    }


    public void addEffect(InGameObject inGameObject) {
        this.effectedBy = inGameObject;
    }

    public InGameObject removeEffect() {
        InGameObject effect = Terrain.findByColor(effectedBy.getColorHex());
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

    @Override
    public String getColorHex() {
        return nation.getTeamColor();
    }
}
