package com.ttu.tarkvaratehnika.empires.gameofempires.gamefield;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.BasicPerson;
import com.ttu.tarkvaratehnika.empires.gameofempires.nation.Nation;

import java.util.HashMap;
import java.util.Map;

//TODO: implement
//Holds 2D array with people instances. Keeps track of how many people left on which team
public class GameField {

    private String mapName;
    private InGameObject[][] field;

    public InGameObject checkCell(int x, int y) {
        return field[x][y];
    }

    public boolean addPersonToCell(BasicPerson basicPerson, int x, int y) {
        return false;
    }

    public boolean removePersonFromCell(int x, int y) {
        return false;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
