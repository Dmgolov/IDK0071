package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/game")
public class SessionController {

    private Gson gson = new Gson();

    private AccountService accountService;
    private SessionService sessionService;

    @Autowired
    public SessionController(AccountService accountService, SessionService sessionService) {
        this.accountService = accountService;
        this.sessionService = sessionService;
    }

    @PostMapping(path = "/mapSettings", consumes = "application/json")
    public @ResponseBody
    String getMapSettings(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                return String.format("{\"width\":%d,\"height\":%d}", searchedLobby.get().getGameField().getMapWidth(),
                        searchedLobby.get().getGameField().getMapHeight());
            }
        }
        return "{\"width\":0,\"height\":0}";
    }

    @PostMapping(path = "/initialMap", consumes = "application/json")
    public @ResponseBody String getInitialMap(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                return gson.toJson(searchedLobby.get().getGameField().getInitialMap());
            }
        }
        return "{}";
    }

    @PostMapping(path = "/state", consumes = "application/json")
    public @ResponseBody String getGameState(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
//        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (!searchedLobby.isPresent()) {
                return sessionService.isGameFinished(lobbyId) ? "{\"status\":\"finished\"}" : "{\"status\":\"notFound\"}";
            }
            int turnNr = gson.fromJson(data, JsonObject.class).get("turnNr").getAsInt();
            String username = gson.fromJson(data, JsonObject.class).get("name").getAsString();
            GameLobby lobby = searchedLobby.get();
            Optional<JsonObject> update = lobby.sendUpdateToUser(username, turnNr);
            if (update.isPresent()) {
                lobby.checkIfEveryoneReceivedUpdate();
                return update.get().toString();
            }
//        }
        return "{\"status\":\"received\"}";
    }

    @PostMapping(path = "/winner", consumes = "application/json")
    public @ResponseBody String getGameWinner(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        return sessionService.getResultForGame(lobbyId);
    }

    @PostMapping(path = "/players", consumes = "application/json")
    public @ResponseBody String getPlayerColors(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                return gson.toJson(searchedLobby.get().getPlayerColors());
            }
        }
        return "{\"status\":\"notFound\"}";
    }
}
