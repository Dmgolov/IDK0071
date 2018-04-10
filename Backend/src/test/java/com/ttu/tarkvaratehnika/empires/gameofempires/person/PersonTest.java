package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class PersonTest {

    private Person person;
    private Nation nation;
    private GameField field;
    private Random random;

    @Before
    public void setUp() {
        nation = Mockito.mock(Nation.class);
        field = Mockito.mock(GameField.class);
        random = Mockito.mock(Random.class);
        person = new Person(nation, field, PersonValues.DEFAULT_STATS);
        person.setRandom(random);
    }

    @Test
    public void testDeathRemovesFromField() {
        person.die();
        Mockito.verify(field, Mockito.times(1)).removePersonFromCell(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testStatChangeChangesPersonAttribute() {
        Mockito.doReturn(0).when(random).nextInt(Mockito.anyInt());
        person.changeRandomStat(2);
        assertEquals(5, person.getVitality());
    }

    @Test
    public void testReproduceAddsNewPersonToField() {
        person.reproduce(0, 0);
        Mockito.verify(field, Mockito.times(1))
                .addPersonToCell(Mockito.any(), Mockito.eq(0), Mockito.eq(0));
    }

    @Test
    public void testMoveCallsForPositionChangeOnField() {
        person.setPositionX(0);
        person.setPositionY(0);
        person.move(1, 1);
        Mockito.verify(field, Mockito.times(1)).movePerson(person, 1, 1, 0, 0);
    }

    @Test
    public void testAddEffectAddNewTerrainEffect() {
        person.addEffect(Terrain.PLAINS);
        assertEquals(Terrain.PLAINS, person.getEffectedBy());
    }

    @Test
    public void testRemoveEffectRemovesOldTerrainEffect() {
        person.addEffect(Terrain.PLAINS);
        person.removeEffect();
        assertNull(person.getEffectedBy());
    }

    @Test
    public void testRemoveEffectReturnsSameTerrainWhichWasAdded() {
        person.addEffect(Terrain.PLAINS);
        assertEquals(Terrain.PLAINS, person.removeEffect());
    }

    @Test
    public void testRemoveFromNationRemovesThisPersonFromNation() {
        person.removeFromNation();
        Mockito.verify(nation, Mockito.times(1)).removePerson(person);
    }

    @Test
    public void testAddToNationAddsThisPersonToNation() {
        person.addToNation();
        Mockito.verify(nation, Mockito.times(1)).addPerson(person);
    }

    @Test
    public void testGetColorHexReturnsColorOfNation() {
        Mockito.doReturn("#ffffff").when(nation).getTeamColor();
        assertEquals("#ffffff", person.getColorHex());
    }

    @Test
    public void testGetFreeNeighbourCellsFindsAllFreeCellsAroundPerson() {
        person.setPositionX(1);
        person.setPositionY(1);
        Mockito.doReturn(3).when(field).getMapWidth();
        Mockito.doReturn(3).when(field).getMapHeight();
        Mockito.doReturn(Terrain.PLAINS).when(field).getObjectInCell(Mockito.anyInt(), Mockito.anyInt());
        List<Coordinates> cells = person.getFreeNeighbourCells();
        assertEquals(8, cells.size());
    }

    @Test
    public void testGetFreeNeighbourCellsFindsAllFreeCellsAroundPersonIfSurroundedByEnemy() {
        person.setPositionX(1);
        person.setPositionY(1);
        Mockito.doReturn(3).when(field).getMapWidth();
        Mockito.doReturn(3).when(field).getMapHeight();
        Person another = Mockito.mock(Person.class);
        Mockito.doReturn(true).when(another).isEnemy(person);
        Mockito.doReturn(another).when(field).getObjectInCell(Mockito.anyInt(), Mockito.anyInt());
        List<Coordinates> cells = person.getFreeNeighbourCells();
        assertEquals(8, cells.size());
    }

    @Test
    public void testIsEnemyReturnsTrueIfDifferentNations() {
        Person another = Mockito.mock(Person.class);
        Mockito.doReturn(Mockito.mock(Nation.class)).when(another).getNation();
        assertTrue(person.isEnemy(another));
    }

    @Test
    public void testHasFreeCellsReturnsTrueIfHasFreeCells() {
        assertTrue(person.hasFreeNeighbourCells(Arrays.asList(new Coordinates(0, 0), new Coordinates(0, 1))));
    }

    @Test
    public void testHasFreeCellsReturnsFalseIfNoFreeCells() {
        assertFalse(person.hasFreeNeighbourCells(new ArrayList<>()));
    }

    @Test
    public void testResistReturnsTrueIfGoodRandom() {
        assertTrue(person.resistDisease());
    }

    @Test
    public void testCenReproduceReturnsTrueIfGoodRandom() {
        assertTrue(person.canReproduce());
    }

    @Test
    public void testGetAttackValue() {
        person.addEffect(Terrain.PLAINS);
        double expectedAttack = 4 * PersonValues.CRIT_MULTIPLIER * Terrain.PLAINS.getAttackMultiplier();
        assertEquals(expectedAttack, person.getAttackStrength(), 0.001);
    }

    @Test
    public void testGetDefenceValue() {
        person.addEffect(Terrain.PLAINS);
        double expectedDefence = 3 * (Terrain.PLAINS.getDefenceMultiplier() + PersonValues.DEFAULT_INTELLIGENCE / 10.0);
        assertEquals(expectedDefence, person.getDefence(), 0.001);
    }

    @Test
    public void testCaptureCellReturnsTrueIfPersonStrongerThanEnemy() {
        Person person = Mockito.mock(Person.class);
        Person another = Mockito.mock(Person.class);
        Mockito.doReturn(4.0).when(person).getAttackStrength();
        Mockito.doReturn(3.0).when(another).getDefence();
        Mockito.doCallRealMethod().when(person).captureCell(Mockito.any());
        assertTrue(person.captureCell(another));
    }

    @Test
    public void testActDeathFromDisease() {
        Person person = Mockito.mock(Person.class);
        Mockito.doCallRealMethod().when(person).act();
        Mockito.doReturn(false).when(person).resistDisease();
        person.act();
        Mockito.verify(person, Mockito.times(1)).die();
    }

    @Test
    public void testActMayResultInReproduction() {
        Person person = Mockito.mock(Person.class);
        Mockito.doCallRealMethod().when(person).act();
        Mockito.doReturn(true).when(person).resistDisease();
        Mockito.doReturn(true).when(person).hasFreeNeighbourCells(Mockito.anyList());
        Mockito.doReturn(true).when(person).canReproduce();
        Mockito.doReturn(Arrays.asList(new Coordinates(0, 0), new Coordinates(0, 1)))
                .when(person).getFreeNeighbourCells();
        Mockito.doCallRealMethod().when(person).setRandom(Mockito.any());
        person.setRandom(random);
        Mockito.doNothing().when(person).mutate(Mockito.anyInt());
        person.act();
        Mockito.verify(person, Mockito.times(1)).reproduce(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testActMayResultInMove() {
        Person person = Mockito.mock(Person.class);
        Mockito.doCallRealMethod().when(person).act();
        Mockito.doReturn(true).when(person).resistDisease();
        Mockito.doReturn(true).when(person).hasFreeNeighbourCells(Mockito.anyList());
        Mockito.doReturn(false).when(person).canReproduce();
        Mockito.doReturn(Arrays.asList(new Coordinates(0, 0), new Coordinates(0, 1)))
                .when(person).getFreeNeighbourCells();
        Mockito.doCallRealMethod().when(person).setRandom(Mockito.any());
        person.setRandom(random);
        Mockito.doNothing().when(person).mutate(Mockito.anyInt());
        person.act();
        Mockito.verify(person, Mockito.times(1)).move(Mockito.anyInt(), Mockito.anyInt());
    }
}