package com.ttu.tarkvaratehnika.empires.gameofempires.nation;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.BasicPerson;

public class Nation implements Runnable {

    private final String username;
    private final String teamColor;

    private final GameLobby session;

    private BasicPerson person;
    private int numOfPeople = 0;
    private boolean ready;

    public Nation(String username, String teamColor, GameLobby session) {
        this.username = username;
        this.teamColor = teamColor;
        this.session = session;
    }

    //TODO: implement spread function for calculating new state of the nation
    public void spread() {

    }

    public boolean setPerson(BasicPerson person) {
        this.person = person;
        return true;
    }

    public boolean setPersonWithStats(int vitality, int dexterity, int intelligence, int growthRate, int strength, int luck) {
        Person person = new Person();
        person.setVitality(vitality);
        person.setDexterity(dexterity);
        person.setIntelligence(intelligence);
        person.setGrowthRate(growthRate);
        person.setStrength(strength);
        person.setLuck(luck);
        return setPerson(person);
    }

    public boolean useTemplateForPerson(String templateName) {
        return false;
    }

    public boolean setDefault() {
        return false;
    }

    public boolean hasSelectedPersonType() {
        return person == null;
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
