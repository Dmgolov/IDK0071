package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//TODO: implement
//Handles sessions
public class SessionController {

    private Set<GameSession> sessions = new HashSet<>();

    public boolean createLobby(String username, String lobbyName, String lobbyPass) {
        return false;
    }

    public boolean connectToLobby(String username, String lobbyName, String lobbyPass) {
        return false;
    }

    public boolean readyCheck(String username, long sessionId) {
        return false;
    }

    public List<String> getSessionNames(String filter) {
        return sessions.stream().map(GameSession::getSessionName)
                .filter(name -> name.contains(filter))
                .collect(Collectors.toList());
    }

    public boolean startSession() {
        return false;
    }

    public boolean endSession() {
        return false;
    }
}
