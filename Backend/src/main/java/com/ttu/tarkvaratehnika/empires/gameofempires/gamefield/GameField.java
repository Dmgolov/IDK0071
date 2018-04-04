package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.ImageConverter;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;

import java.io.IOException;
import java.util.*;

public class GameField {

    private GameMap gameMap;
    private InGameObject[][] field;
    private final Map<Coordinates, Person> lastUpdate = new HashMap<>();
    private String mapName = "worldMap.png";

    public void loadField() throws IOException {
        field = ImageConverter.createField(mapName);
    }

    public JsonArray getInitialMap() throws IOException {
        JsonArray array = new JsonArray();
        System.out.println("Width " + getMapWidth());
        System.out.println("Height " + getMapHeight());
        for (int y = 0; y < getMapHeight(); y++) {
            for (int x = 0; x < getMapWidth(); x++) {
                JsonObject object = new JsonObject();
                object.addProperty("x", x);
                object.addProperty("y", y);
                object.addProperty("color", field[y][x].getColorHex());
                array.add(object);
            }
        }
        return array;
    }

    public InGameObject getObjectInCell(int x, int y) {
        return field[y][x];
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
            int x = coordinates.getX(), y = coordinates.getY();
            singleCell.addProperty("x", x);
            singleCell.addProperty("y", y);
            Optional<Person> optionalPerson = Optional.ofNullable(updatedCells.get(coordinates));
            if (optionalPerson.isPresent()) {
                singleCell.addProperty("color", updatedCells.get(coordinates).getColorHex());
            } else {
                // TODO: redo so that returns actual color of terrain
                singleCell.addProperty("color", field[y][x].getColorHex());
            }
            updatedCellsAsJsonArray.add(singleCell);
        }
        return updatedCellsAsJsonArray;
    }

    // private in order to restrict modification simultaneously from multiple threads
    private void addPersonToCell(Person person, int x, int y) {
        InGameObject object = field[y][x];
        if (object instanceof Person) {
            object = ((Person) object).removeEffect();
        }
        person.addEffect(object);
        field[y][x] = person;
    }

    // private in order to restrict modification simultaneously from multiple threads
    private void removePersonFromCell(int x, int y) {
        InGameObject object = field[y][x];
        if (object instanceof Person) {
            field[y][x] = ((Person) object).getEffectedBy();
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

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }
}
