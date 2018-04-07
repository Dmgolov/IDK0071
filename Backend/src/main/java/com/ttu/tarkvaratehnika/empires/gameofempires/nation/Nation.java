package com.ttu.tarkvaratehnika.empires.gameofempires.nation;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.PersonValues;

import java.io.IOException;
import java.util.*;

public class Nation implements Runnable {

    private final String username;
    private final String teamColor;

    private final GameLobby session;
    private final GameField field;

    private Person person;
    private boolean ready;

    private Set<Person> people = new HashSet<>();
    private Map<Coordinates, Person> updatedPositions = new HashMap<>();

    public Nation(String username, String teamColor, GameLobby session) {
        this.username = username;
        this.teamColor = teamColor;
        this.session = session;
        field = session.getGameField();
    }

    private void spread() throws IOException {
        //here initiates finding new positions for people
        List<Person> tempPeople = new ArrayList<>(people);
        for (Person person : tempPeople) {
            person.act();
        }
        //this shares data about nation updated state to the map
        session.addUpdatedState(updatedPositions);
        updatedPositions = new HashMap<>();
    }

    public void setPersonToCoordinates(int x, int y) {
        Person person = new Person(this.person);
        person.setPositionX(x);
        person.setPositionY(y);
        people.add(person);
        updatedPositions.put(new Coordinates(x, y), person);
    }

    public void movePersonToCoordinates(Person person, int newX, int newY, int oldX, int oldY) {
        person.setPositionX(newX);
        person.setPositionY(newY);
        updatedPositions.put(new Coordinates(oldX, oldY), null);
        updatedPositions.put(new Coordinates(newX, newY), person);
    }

    public void removePersonFromCoordinates(int x, int y) {
        InGameObject object = field.getObjectInCell(x, y);
        if (object instanceof Person) {
            people.remove(object);
            updatedPositions.put(new Coordinates(x, y), null);
            System.out.println("removed");
        } else {
            System.out.println("not instance of person: " + object.toString());
        }
    }

    public void setStartingLocation() {
        Random random = new Random();
        int positionX = random.nextInt(field.getMapWidth());
        int positionY = random.nextInt(field.getMapHeight());
        InGameObject cell = field.getObjectInCell(positionX, positionY);
        while (cell instanceof Person || cell instanceof Terrain && !((Terrain) cell).isPassable()) {
            positionX = random.nextInt(field.getMapWidth());
            positionY = random.nextInt(field.getMapHeight());
            cell = field.getObjectInCell(positionX, positionY);
        }
        addFirstPersonToField(positionX, positionY);
    }

    private void addFirstPersonToField(int x, int y) {
        person = new Person(this.person);
        person.setPositionX(x);
        person.setPositionY(y);
        people.add(person);
        field.addPersonToCell(person, x, y);
    }

    public boolean isActive() {
        return people.size() > 0;
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

    public Map<Coordinates, Person> getUpdatedPositions() {
        return updatedPositions;
    }

    public Set<Person> getPeople() {
        return people;
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
        return people.size();
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public void run() {
        while (isActive()) {
            System.out.println(username + " has " + people.size() + " people");
            //System.out.println("Spreading " + username);
            try {
                spread();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println("Ending turn " + username);
            synchronized (session) {
                session.endTurn();
                try {
                    System.out.println("Waiting " + username);
                    session.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //System.out.println("Shutting down " + username);
    }
}
