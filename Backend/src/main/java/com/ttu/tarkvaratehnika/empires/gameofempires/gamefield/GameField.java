package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.BasicPerson;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.util.HashMap;
import java.util.Map;

public class GameField {

    private String mapName;
    private InGameObject[][] field;

    //TODO: load a map by name
    public void loadField() {

    }

    public InGameObject checkCell(int x, int y) {
        return field[x][y];
    }

    //TODO: optimise map update this way, so each nation's list of people corresponded to the actual game state
    /*
    Right now if person of one nation acquires new cell or loses old cell, it can add/remove a person to/from its list.
    However, if another nation also does anything with this cell, and later overwrites value for coordinates key, then values
    in first nation list will not correspond to the actual game state
    Example: new person saved in cell (3, 3). Another nation overwrites it with its own person in cell (3, 3).
    First nation thinks it still has person there, but it is actually not there.
     */
    public void updateMap(Map<Coordinates, Person> updatedCells) {
        for (Coordinates key : updatedCells.keySet()) {
            if (updatedCells.get(key) != null) {
                addPersonToCell(updatedCells.get(key), key.getX(), key.getY());
            } else {
                removePersonFromCell(key.getX(), key.getY());
            }
        }
    }

    public void addPersonToCell(Person person, int x, int y) {
        InGameObject object = field[x][y];
        if (object instanceof Person) {
            object = ((Person) object).removeEffect();
        }
        person.addEffect(object);
        field[x][y] = person;
    }

    public void removePersonFromCell(int x, int y) {
        InGameObject object = field[x][y];
        if (object instanceof Person) {
            field[x][y] = ((Person) object).getEffectedBy();
        }
    }

    public int getMapWidth() {
        return field[0].length;
    }

    public int getMapHeight() {
        return field.length;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
