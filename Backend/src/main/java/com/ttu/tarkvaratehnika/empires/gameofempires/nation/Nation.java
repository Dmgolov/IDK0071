package com.ttu.tarkvaratehnika.empires.gameofempires.nation;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.GameField;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.PersonValues;

import java.util.*;

public class Nation implements Runnable {

    private final String username;
    private final String teamColor;

    private final GameLobby session;
    private final GameField field;

    private Person person;
    private boolean ready;

    private final Set<Person> people = new HashSet<>();

    public Nation(String username, String teamColor, GameLobby session) {
        this.username = username;
        this.teamColor = teamColor;
        this.session = session;
        field = session.getGameField();
    }

    void spread() {
        List<Person> tempPeople;
        synchronized (people) {
            tempPeople = new ArrayList<>(people);
        }
        for (Person person : tempPeople) {
            person.act();
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
        synchronized (people) {
            Person person = new Person(this.person);
            people.add(person);
            field.addPersonToCell(person, x, y);
        }
    }

    public boolean isActive() {
        return people.size() > 0;
    }

    public void removePerson(Person person) {
        synchronized (people) {
            people.remove(person);
        }
    }

    public void addPerson(Person person) {
        synchronized (people) {
            people.add(person);
        }
    }

    public void setPersonWithStats(Map<String, Integer> stats) {
        person = new Person(this, field, stats.isEmpty() ? PersonValues.DEFAULT_STATS : stats);
    }

    public void setDefaultPerson() {
        int total = session.getProperties().getNationPoints();
        Map<String, Integer> stats = new HashMap<>();
        int points = total / PersonValues.DEFAULT_STATS.size();
        stats.put(PersonValues.VITALITY, points);
        stats.put(PersonValues.STRENGTH, points);
        stats.put(PersonValues.DEXTERITY, points);
        stats.put(PersonValues.INTELLIGENCE, points);
        stats.put(PersonValues.LUCK, points);
        stats.put(PersonValues.REPRODUCTION, total - PersonValues.DEFAULT_STATS.size() * points);
        this.person = new Person(this, field, stats);
    }

    public boolean hasSelectedPersonType() {
        return person != null;
    }

    public Set<Person> getPeople() {
        synchronized (people) {
            return people;
        }
    }

    public Set<Person> getPeopleCopy() {
        synchronized (people) {
            return new HashSet<>(people);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public int getNumOfPeople() {
        return people.size();
    }

    GameField getField() {
        return field;
    }

    Person getPerson() {
        return person;
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
            spread();
            synchronized (session) {
                session.endTurn();
                try {
                    session.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
