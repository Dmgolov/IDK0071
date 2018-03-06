package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Land;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.util.*;

public class GameField {

    private GameMap gameMap;
    private InGameObject[][] field;
    private final Map<Coordinates, Person> lastUpdate = new HashMap<>();

    public void loadField() {
        field = new InGameObject[gameMap.getMapWidth()][gameMap.getMapHeight()];
        for (int row = 0; row < getMapWidth(); row++) {
            for (int col = 0; col < getMapHeight(); col++) {
                field[row][col] = new Land();
            }
        }
    }

    public JsonArray getInitialMap() {
        JsonArray array = new JsonArray();
        for (int row = 0; row < getMapWidth(); row++) {
            for (int col = 0; col < getMapHeight(); col++) {
                JsonObject object = new JsonObject();
                object.addProperty("x", row);
                object.addProperty("y", col);
                object.addProperty("color", Land.COLOR_HEX);
                array.add(object);
            }
        }
        return array;
    }

    public InGameObject getObjectInCell(int x, int y) {
        return field[x][y];
    }

    public void clearLastUpdate() {
        synchronized (lastUpdate) {
            lastUpdate.clear();
        }
    }

    public boolean hasLastUpdate() {
        synchronized (lastUpdate) {
            return !lastUpdate.isEmpty();
        }
    }

    public void updateMap(Map<Coordinates, Person> updatedCells) {
        for (Coordinates key : updatedCells.keySet()) {
            if (updatedCells.get(key) != null) {
                addPersonToCell(updatedCells.get(key), key.getX(), key.getY());
            } else {
                removePersonFromCell(key.getX(), key.getY());
            }
        }
        synchronized (lastUpdate) {
            lastUpdate.putAll(updatedCells);
        }
    }

    public JsonObject getLastMapUpdate(int turnNr) {
        JsonObject jsonToReturn = new JsonObject();
        Map<Coordinates, Person> totalUpdate;
        synchronized (lastUpdate) {
            totalUpdate = new HashMap<>(lastUpdate);
        }
        turnNr++;
        JsonArray updatedCells = getUpdatedCellsAsJsonArray(totalUpdate);
        jsonToReturn.addProperty("turnNr", turnNr);
        jsonToReturn.add("update", updatedCells);
        jsonToReturn.addProperty("status", "running");
        return jsonToReturn;
    }

    // populating array with map updates since turn N
    private JsonArray getUpdatedCellsAsJsonArray(Map<Coordinates, Person> updatedCells) {
        JsonArray updatedCellsAsJsonArray = new JsonArray();
        for (Coordinates coordinates : updatedCells.keySet()) {
            JsonObject singleCell = new JsonObject();
            singleCell.addProperty("x", coordinates.getX());
            singleCell.addProperty("y", coordinates.getY());
            Optional<Person> optionalPerson = Optional.ofNullable(updatedCells.get(coordinates));
            if (optionalPerson.isPresent()) {
                singleCell.addProperty("color", updatedCells.get(coordinates).getNation().getTeamColor());
            } else {
                // TODO: redo so that returns actual color of terrain
                singleCell.addProperty("color", Land.COLOR_HEX);
            }
            updatedCellsAsJsonArray.add(singleCell);
        }
        return updatedCellsAsJsonArray;
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
        return field.length;
    }

    public int getMapHeight() {
        return field[0].length;
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
