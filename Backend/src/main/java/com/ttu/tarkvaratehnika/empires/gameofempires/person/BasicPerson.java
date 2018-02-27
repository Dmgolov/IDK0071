package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;

public interface BasicPerson extends InGameObject {

    void move();

    boolean reproduce();

    boolean resistDisease();

    boolean captureCell(Person another);
}
