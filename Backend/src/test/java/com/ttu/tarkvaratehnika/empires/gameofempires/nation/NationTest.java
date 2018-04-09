package com.ttu.tarkvaratehnika.empires.gameofempires.nation;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.PersonValues;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class NationTest {

    private Nation nation;
    private GameField field;

    @Before
    public void setUp() {
        GameLobby gameLobby = Mockito.mock(GameLobby.class);
        field = Mockito.mock(GameField.class);
        Mockito.doReturn(field).when(gameLobby).getGameField();
        nation = new Nation(null, null, gameLobby);
    }

    @Test
    public void testSpreadMakesPeopleAct() {
        Person person = Mockito.mock(Person.class);
        nation.addPerson(person);
        nation.spread();
        Mockito.verify(person, Mockito.times(1)).act();
    }

    @Test
    public void testAddPersonToFirstLocationAddsNewPersonToNation() {
        Mockito.doReturn(1).when(field).getMapWidth();
        Mockito.doReturn(1).when(field).getMapHeight();
        Mockito.doReturn(Terrain.PLAINS).when(field).getObjectInCell(Mockito.anyInt(), Mockito.anyInt());
        nation.setPersonWithStats(PersonValues.DEFAULT_STATS);
        nation.setStartingLocation();
        assertEquals(1, nation.getNumOfPeople());
    }

    @Test
    public void testAddPersonAddsPersonToPeopleList() {
        nation.addPerson(Mockito.mock(Person.class));
        assertEquals(1, nation.getNumOfPeople());
    }

    @Test
    public void testRemovePersonRemovesPersonFromPeopleList() {
        Person person = Mockito.mock(Person.class);
        nation.addPerson(person);
        nation.removePerson(person);
        assertEquals(0, nation.getNumOfPeople());
    }

    @Test
    public void testConstructorCopiesGameFieldFromLobby() {
        assertNotNull(nation.getField());
    }

    @Test
    public void testIsActiveWhenNationHasPeople() {
        nation.addPerson(Mockito.mock(Person.class));
        assertTrue(nation.isActive());
    }

    @Test
    public void testIsNotActiveWhenNationHasNoPeople() {
        assertFalse(nation.isActive());
    }

    @Test
    public void testSetPersonWithStatsCreatesNewPersonObject() {
        nation.setPersonWithStats(PersonValues.DEFAULT_STATS);
        assertNotNull(nation.getPerson());
    }
}