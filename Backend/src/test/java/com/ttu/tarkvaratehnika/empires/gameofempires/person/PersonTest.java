package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
}