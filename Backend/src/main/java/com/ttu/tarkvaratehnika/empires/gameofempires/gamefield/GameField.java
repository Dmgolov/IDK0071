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

    private InGameObject[][] field = new InGameObject[][]{};
    private Set<Coordinates> currentUpdate = new HashSet<>();
    private final Set<Coordinates> lastUpdate = new HashSet<>();
    private String mapName;

    public GameField(String mapName) {
        this.mapName = mapName;
        try {
            loadField();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadField() throws IOException {
        field = ImageConverter.createField(mapName);
    }

    public JsonArray getInitialMap() {
        JsonArray array = new JsonArray();
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

    public void updateMap() {
        synchronized (lastUpdate) {
            synchronized (currentUpdate) {
                lastUpdate.addAll(currentUpdate);
            }
            currentUpdate = new HashSet<>();
        }
    }

    public JsonObject getLastMapUpdate(int turnNr) {
        JsonObject jsonToReturn = new JsonObject();
        Set<Coordinates> totalUpdate;
        synchronized (lastUpdate) {
            totalUpdate = new HashSet<>(lastUpdate);
        }
        turnNr++;
        JsonArray updatedCells = getUpdatedCellsAsJsonArray(totalUpdate);
        jsonToReturn.addProperty("turnNr", turnNr);
        jsonToReturn.addProperty("status", "running");
        jsonToReturn.add("update", updatedCells);
        return jsonToReturn;
    }

    JsonArray getUpdatedCellsAsJsonArray(Set<Coordinates> updatedCells) {
        JsonArray updatedCellsAsJsonArray = new JsonArray();
        for (Coordinates coordinates : updatedCells) {
            JsonObject singleCell = new JsonObject();
            int x = coordinates.getX(), y = coordinates.getY();
            singleCell.addProperty("x", x);
            singleCell.addProperty("y", y);
            singleCell.addProperty("color", field[y][x].getColorHex());
            updatedCellsAsJsonArray.add(singleCell);
        }
        return updatedCellsAsJsonArray;
    }

    public void addPersonToCell(Person person, int x, int y) {
        person.setPositionX(x);
        person.setPositionY(y);
        synchronized (field) {
            InGameObject object = field[y][x];
            if (object instanceof Person) {
                Person another = (Person) object;
                person.addEffect(another.getEffectedBy());
                if (person.captureCell(another)) {
                    another.removeFromNation();
                    synchronized (currentUpdate) {
                        currentUpdate.add(new Coordinates(x, y));
                    }
                    person.addEffect(another.removeEffect());
                    person.addToNation();
                    field[y][x] = person;
                } else {
                    person.removeFromNation();
                    person.removeEffect();
                }
            } else {
                person.addEffect((Terrain) object);
                field[y][x] = person;
                person.addToNation();
                synchronized (currentUpdate) {
                    currentUpdate.add(new Coordinates(x, y));
                }
            }
        }
    }

    public void removePersonFromCell(int x, int y) {
        synchronized (field) {
            InGameObject object = field[y][x];
            if (object instanceof Person) {
                Person person = (Person) object;
                field[y][x] = person.removeEffect();
                person.removeFromNation();
                synchronized (currentUpdate) {
                    currentUpdate.add(new Coordinates(x, y));
                }
            }
        }
    }

    public void movePerson(Person person, int newX, int newY, int oldX, int oldY) {
        synchronized (field) {
            removePersonFromCell(oldX, oldY);
            addPersonToCell(person, newX, newY);
        }
    }

    public int getMapWidth() {
        return field[0].length;
    }

    public int getMapHeight() {
        return field.length;
    }

    public void setGameMap(String mapName) {
        this.mapName = mapName;
    }


    void setField(InGameObject[][] field) {
        this.field = field;
    }

    Set<Coordinates> getCurrentUpdate() {
        return currentUpdate;
    }

    Set<Coordinates> getLastUpdate() {
        return lastUpdate;
    }
}
