package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoordinatesTest {

    private Coordinates coordinate;

    @Before
    public void setUp() {
        coordinate = new Coordinates(0, 0);
    }

    @Test
    public void testSamePointSameHashCode() {
        assertEquals(coordinate.hashCode(), new Coordinates(0, 0).hashCode());
    }

    @Test
    public void testDifferentPointsDifferentHashCodes() {
        assertNotEquals(coordinate.hashCode(), new Coordinates(0, 1).hashCode());
    }

    @Test
    public void testSamePointEquals() {
        assertTrue(coordinate.equals(new Coordinates(0, 0)));
    }

    @Test
    public void testDifferentPointsNotEquals() {
        assertFalse(coordinate.equals(new Coordinates(0, 1)));
    }

    @Test
    public void testToString() {
        assertEquals("[0, 0]", coordinate.toString());
    }
}