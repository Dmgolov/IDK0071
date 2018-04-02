package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;

import java.io.IOException;
import java.util.List;

public interface BasicPerson extends InGameObject {

    void act() throws IOException;

    List<Coordinates> getFreeNeighbourCells() throws IOException;

    boolean reproduce();

    boolean resistDisease();

    boolean captureCell(Person another);
}
