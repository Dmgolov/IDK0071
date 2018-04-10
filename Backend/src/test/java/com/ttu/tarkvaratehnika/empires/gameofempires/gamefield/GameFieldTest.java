package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class GameFieldTest {

    private GameField gameField;
    private Person person;
    private Person another;

    @Before
    public void setUp() {
        InGameObject[][] field = new InGameObject[3][2];
        field[0][0] = Terrain.PLAINS;
        person = Mockito.mock(Person.class);
        another = Mockito.mock(Person.class);
        gameField = new GameField(null);
        gameField.setField(field);
    }

    @Test
    public void testAddPersonToCellAddsNewStateToCurrentUpdate() {
        gameField.addPersonToCell(person, 0, 0);
        assertTrue(gameField.getCurrentUpdate().contains(new Coordinates(0, 0)));
    }

    @Test
    public void testRemovePersonFromCellAddsNewStateToCurrentUpdate() {
        InGameObject[][] field = new InGameObject[2][2];
        field[0][0] = person;
        gameField.setField(field);
        gameField.removePersonFromCell(0, 0);
        assertTrue(gameField.getCurrentUpdate().contains(new Coordinates(0, 0)));
    }

    @Test
    public void testAddPersonToCellAddsPersonToTheField() {
        gameField.addPersonToCell(person, 0, 0);
        assertEquals(person, gameField.getObjectInCell(0, 0));
    }

    @Test
    public void testAddPersonToCellDoesNotChangePersonIfCellOccupiedAndPersonLoses() {
        Mockito.doReturn(false).when(person).captureCell(Mockito.any());
        gameField.addPersonToCell(another, 0,0);
        gameField.addPersonToCell(person, 0, 0);
        assertEquals(another, gameField.getObjectInCell(0, 0));
    }

    @Test
    public void testAddPersonToCellChangesPersonIfCellOccupiedAndPersonWins() {
        Mockito.doReturn(true).when(person).captureCell(Mockito.any());
        gameField.addPersonToCell(another, 0,0);
        gameField.addPersonToCell(person, 0, 0);
        assertEquals(person, gameField.getObjectInCell(0, 0));
    }

    @Test
    public void testRemovePersonFromCellRemovesPerson() {
        gameField.addPersonToCell(person, 0, 0);
        Mockito.doReturn(Terrain.PLAINS).when(person).removeEffect();
        gameField.removePersonFromCell(0, 0);
        assertEquals(Terrain.PLAINS, gameField.getObjectInCell(0, 0));
    }

    @Test
    public void testMovePersonAddsPersonToNewCell() {
        gameField.movePerson(person, 0, 0, 0, 1);
        assertEquals(person, gameField.getObjectInCell(0, 0));
    }

    @Test
    public void testMovePersonRemovesPersonFromOldCell() {
        gameField.movePerson(person, 0, 0, 0, 1);
        assertNull(gameField.getObjectInCell(0, 1));
    }

    @Test
    public void testGetObjectInCellReturnsProperObject() {
        InGameObject[][] field = new InGameObject[2][2];
        field[0][0] = person;
        gameField.setField(field);
        assertEquals(person, gameField.getObjectInCell(0,0));
    }

    @Test
    public void testGetObjectInCellReturnsTerrainIfHasNoPeople() {
        assertEquals(Terrain.PLAINS, gameField.getObjectInCell(0,0));
    }

    @Test
    public void testAddPersonToCellUpdatesPersonCoordinates() {
        gameField.addPersonToCell(person, 0, 1);
        Mockito.verify(person, Mockito.times(1)).setPositionX(0);
        Mockito.verify(person, Mockito.times(1)).setPositionY(1);
    }

    @Test
    public void testAddPersonToCellAddsEffectOnSuccess() {
        gameField.addPersonToCell(person, 0, 0);
        Mockito.verify(person, Mockito.times(1)).addEffect(Terrain.PLAINS);
    }

    @Test
    public void testRemovePersonFromCellRemovesEffect() {
        gameField.addPersonToCell(person, 0, 0);
        gameField.removePersonFromCell(0, 0);
        Mockito.verify(person, Mockito.times(1)).removeEffect();
    }

    @Test
    public void testGetMapWidthReturnsProperValue() {
        assertEquals(2, gameField.getMapWidth());
    }

    @Test
    public void testGetMapHeightReturnsProperValue() {
        assertEquals(3, gameField.getMapHeight());
    }

    @Test
    public void testUpdateForUserHasAllUpdatedCells() {
        gameField.addPersonToCell(person, 0, 0);
        gameField.addPersonToCell(person, 0, 1);
        JsonArray jsonArray = gameField.getUpdatedCellsAsJsonArray(gameField.getCurrentUpdate()).getAsJsonArray();
        assertEquals(2, jsonArray.size());
    }

    @Test
    public void testUpdateForUserHasRightPersonColor() {
        gameField.addPersonToCell(person, 0, 0);
        Mockito.doReturn("#ffffff").when(person).getColorHex();
        JsonArray jsonArray = gameField.getUpdatedCellsAsJsonArray(gameField.getCurrentUpdate()).getAsJsonArray();
        assertEquals("#ffffff", jsonArray.get(0).getAsJsonObject().get("color").getAsString());
    }

    @Test
    public void testUpdateForUserHasProperCoordinates() {
        gameField.addPersonToCell(person, 0, 1);
        Mockito.doReturn("#ffffff").when(person).getColorHex();
        Mockito.doReturn(0).when(person).getPositionX();
        Mockito.doReturn(1).when(person).getPositionY();
        JsonArray jsonArray = gameField.getUpdatedCellsAsJsonArray(gameField.getCurrentUpdate()).getAsJsonArray();
        assertEquals(0, jsonArray.get(0).getAsJsonObject().get("x").getAsInt());
        assertEquals(1, jsonArray.get(0).getAsJsonObject().get("y").getAsInt());
    }

    @Test
    public void testUpdateForUserHasAllRequiredKeys() {
        gameField.addPersonToCell(person, 0, 0);
        JsonObject response = gameField.getLastMapUpdate(0);
        assertTrue(response.has("turnNr"));
        assertTrue(response.has("update"));
        assertTrue(response.has("status"));
    }

    @Test
    public void testUpdateForUserIncrementsTurnNr() {
        gameField.addPersonToCell(person, 0, 0);
        JsonObject response = gameField.getLastMapUpdate(0);
        assertEquals(1, response.get("turnNr").getAsInt());
    }

    @Test
    public void testUpdateMapSavesLastTurnUpdate() {
        gameField.addPersonToCell(person, 0, 0);
        gameField.updateMap();
        assertEquals(1, gameField.getLastUpdate().size());
    }

    @Test
    public void testClearLastUpdateRemovesAllUnneededValues() {
        gameField.addPersonToCell(person, 0, 0);
        gameField.updateMap();
        gameField.clearLastUpdate();
        assertEquals(0, gameField.getLastUpdate().size());
    }

    @Test
    public void testHasLastUpdateReturnsTrueIfThereAreValuesForUserToUpdate() {
        gameField.addPersonToCell(person, 0, 0);
        gameField.updateMap();
        assertTrue(gameField.hasLastUpdate());
        gameField.clearLastUpdate();
        assertFalse(gameField.hasLastUpdate());
    }

    @Test
    public void testGetInitialMapFillsJsonArrayWithAllValues() {
        InGameObject[][] field = new InGameObject[1][1];
        field[0][0] = Terrain.PLAINS;
        gameField.setField(field);
        JsonArray array = gameField.getInitialMap();
        assertEquals(1, array.size());
    }

    @Test
    public void testGetInitialMapFillsJsonArrayWithRightValues() {
        InGameObject[][] field = new InGameObject[1][1];
        field[0][0] = Terrain.PLAINS;
        gameField.setField(field);
        JsonObject object = gameField.getInitialMap().get(0).getAsJsonObject();
        assertEquals(0, object.get("x").getAsInt());
        assertEquals(0, object.get("y").getAsInt());
        assertEquals(Terrain.PLAINS.getColorHex(), object.get("color").getAsString());
    }
}