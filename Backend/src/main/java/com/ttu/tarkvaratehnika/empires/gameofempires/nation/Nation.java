package com.ttu.tarkvaratehnika.empires.gameofempires.nation;

import com.ttu.tarkvaratehnika.empires.gameofempires.person.Person;
import com.ttu.tarkvaratehnika.empires.gameofempires.person.BasicPerson;

import java.awt.*;

//TODO: add more functionality, if required
//Instance of user, created for a game session, deleted after match end or leaving session
//Currently uses Colors for distinction, maybe use something else
public class Nation {

    private final long id;
    private final String username;
    private final Color team;

    private long sessionId;

    private BasicPerson person;

    public Nation(long id, String username, Color team) {
        this.id = id;
        this.username = username;
        this.team = team;
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

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Color getTeam() {
        return team;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
}
