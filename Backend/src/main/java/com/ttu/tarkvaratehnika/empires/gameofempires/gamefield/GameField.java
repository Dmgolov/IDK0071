package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Land;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.util.Map;

public class GameField {

    private GameMap gameMap;
    private InGameObject[][] field;

    public void loadField() {
        field = new InGameObject[gameMap.getMapWidth()][gameMap.getMapHeight()];
        for (int row = 0; row < getMapWidth(); row++) {
            for (int col = 0; col < getMapHeight(); col++) {
                field[row][col] = new Land();
            }
        }
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

    // private in order to restrict modification simultaneously from multiple threads
    private void addPersonToCell(Person person, int x, int y) {
        InGameObject object = field[x][y];
        if (object instanceof Person) {
            object = ((Person) object).removeEffect();
        }
        person.addEffect(object);
        field[x][y] = person;
    }

    // private in order to restrict modification simultaneously from multiple threads
    private void removePersonFromCell(int x, int y) {
        InGameObject object = field[x][y];
        if (object instanceof Person) {
            field[x][y] = ((Person) object).getEffectedBy();
        }
    }

    public String getMapName() {
        return gameMap.getMapName();
    }

    public int getMapWidth() {
        return field[0].length;
    }

    public int getMapHeight() {
        return field.length;
    }

    public boolean isMapSet() {
        return gameMap != null;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}
