package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import com.ttu.tarkvaratehnika.empires.gameofempires.player.Nation;

import java.util.HashMap;
import java.util.Map;

//TODO: implement
//Holds 2D array with people instances. Keeps track of how many people left on which team
public class GameField {

    private long sessionId;

    private InGameObject[][] field;
    private Map<Nation, Integer> teamTroops = new HashMap<>();

    //Main method for calculating new positions of active people
    public void moveTroops() {

    }

    public boolean addPersonToCell(Person person, int x, int y) {
        return false;
    }

    public boolean removePersonFromCell(int x, int y) {
        return false;
    }

    public int getTroopsCountForPlayer(Nation nation) {
        return teamTroops.get(nation);
    }
}
