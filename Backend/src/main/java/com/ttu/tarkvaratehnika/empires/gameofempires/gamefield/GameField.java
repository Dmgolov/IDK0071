package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.util.Map;

public class GameField {

    private String mapName;
    private InGameObject[][] field;

    //TODO: load a map by name
    public void loadField() {

    }

    public InGameObject getObjectInCell(int x, int y) {
        return field[x][y];
    }

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
