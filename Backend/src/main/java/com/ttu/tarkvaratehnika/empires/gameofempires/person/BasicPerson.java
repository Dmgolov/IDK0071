package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;

import java.util.List;

public interface BasicPerson extends InGameObject {

    void act();

    List<Coordinates> getFreeNeighbourCells();

    boolean reproduce();

    boolean resistDisease();

    boolean captureCell(Person another);
}
