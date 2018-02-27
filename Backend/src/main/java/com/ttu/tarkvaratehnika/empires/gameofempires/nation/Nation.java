package com.ttu.tarkvaratehnika.empires.gameofempires.nation;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.PersonValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nation implements Runnable {

    private final String username;
    private final String teamColor;

    private final GameLobby session;
    private final GameField field;

    private Person person;
    private int numOfPeople = 0;
    private boolean ready;

    private List<Person> people = new ArrayList<>();
    private Map<Coordinates, Person> updatedPositions = new HashMap<>();

    public Nation(String username, String teamColor, GameLobby session) {
        this.username = username;
        this.teamColor = teamColor;
        this.session = session;
        field = session.getGameField();
    }

    //TODO: implement spread function for calculating new state of the nation
    public void spread() {
        //here initiates finding new positions for people
        //[CODE]
        //this shares data about nation updated state to the map
        session.addUpdatedState(updatedPositions);
        updatedPositions.clear();
    }

    public void addNewPerson(Coordinates coordinates) {
        Person person = new Person(this.person);
        person.setPositionX(coordinates.getX());
        person.setPositionY(coordinates.getY());
        people.add(person);
        updatedPositions.put(coordinates, person);
    }

    public void removePerson(Coordinates coordinates) {
        InGameObject object = field.checkCell(coordinates.getX(),coordinates.getY());
        if (object instanceof Person) {
            people.remove(object);
            updatedPositions.put(coordinates, null);
        }
    }

    public boolean isExtinct() {
        return numOfPeople <= 0;
    }

    public void removePerson(Person person) {
        people.remove(person);
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public void setPersonWithStats(Map<String, Integer> stats) {
        person = new Person(this, field, stats.isEmpty() ? PersonValues.DEFAULT_STATS : stats);
    }

    public void setDefaultPerson() {
        this.person = new Person(this, field, PersonValues.DEFAULT_STATS);
    }

    public boolean hasSelectedPersonType() {
        return person != null;
    }

    public Person getPerson() {
        return person;
    }

    public String getUsername() {
        return username;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public GameLobby getSession() {
        return session;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public void run() {
        while (numOfPeople > 0) {
            spread();
            try {
                synchronized (session) {
                    session.endTurn();
                    session.wait();
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
        synchronized (session) {
            session.endTurn();
        }
    }
}
