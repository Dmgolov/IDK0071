package com.ttu.tarkvaratehnika.empires.gameofempires.player;

import java.awt.*;

//TODO: add more functionality, if required
//Instance of user, created for a game session, deleted after match end or leaving session
//Currently uses Colors for distinction, maybe use something else
public class Nation {

    private final long id;
    private final String name;
    private final Color team;

    private long sessionId;

    public Nation(long id, String name, Color team) {
        this.id = id;
        this.name = name;
        this.team = team;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
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
