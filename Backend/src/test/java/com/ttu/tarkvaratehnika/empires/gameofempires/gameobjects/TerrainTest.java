package com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects;

import org.junit.Test;

import static org.junit.Assert.*;

public class TerrainTest {

    @Test
    public void testFindByColorReturnsProperTerrain() {
        assertEquals(Terrain.RIVER, Terrain.findByColor(Terrain.RIVER.getColorHex()));
    }

    @Test
    public void testFindByColorReturnsDefaultWhenNoSuchColorFound() {
        assertEquals(Terrain.PLAINS, Terrain.findByColor("some random string"));
    }
}