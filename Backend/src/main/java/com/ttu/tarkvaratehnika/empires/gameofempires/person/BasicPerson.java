package com.ttu.tarkvaratehnika.empires.gameofempires.person;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;

//TODO: think of required methods
//Interface for person
public interface BasicPerson extends InGameObject {

    boolean reproduce();

    boolean resistDisease();

    Person fight(Person another);
}
