package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;

import java.util.*;
import java.util.stream.Collectors;

//Handles lobbies
public class SessionController {

    private Set<GameLobby> lobbies = new HashSet<>();

    public long createLobby(String username, String lobbyName, String lobbyPass) {
        GameLobby session = new GameLobby(this);
        lobbies.add(session);
        session.setLobbyName(lobbyName);
        session.setLobbyPass(lobbyPass);
        session.enterSession(username);
        return session.getLobbyId();
    }

    public boolean connectToLobby(String username, long lobbyId, String lobbyPass) {
        Optional<GameLobby> searchedSession = lobbies.stream()
                .filter(session -> session.getLobbyId() == lobbyId && session.getLobbyPass().equals(lobbyPass))
                .findFirst();
        return searchedSession.isPresent() && searchedSession.get().enterSession(username);
    }

    //TODO: add nation stats
    public void readyCheck(String username, long lobbyId, boolean ready, Map<String, Integer> stats) {
        lobbies.stream()
                .filter(session -> session.getLobbyId() == lobbyId)
                .findFirst().ifPresent(session -> session.readyCheck(username, ready, stats));
    }

    public List<Map<String, Object>> checkPlayerState(long lobbyId) {
        Optional<GameLobby> gameLobby = lobbies.stream().filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findAny();
        return gameLobby.isPresent() ? gameLobby.get().checkPlayerState() : null;
    }

    public List<String> getLobbyNames(String filter) {
        return lobbies.stream().map(GameLobby::getLobbyName)
                .filter(name -> name.contains(filter))
                .collect(Collectors.toList());
    }

    public boolean startLobby(long lobbyId) {
        Optional<GameLobby> searchedSession = lobbies.stream()
                .filter(session -> session.getLobbyId() == lobbyId)
                .findFirst();
        return searchedSession.isPresent() && searchedSession.get().startSession();
    }

    public void sendSessionResult(String winnerName, long lobbyId) {

    }
}
