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
        JsonObject response;
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                response = generateDefaultResponse("success", "null");
                response.addProperty("width", searchedLobby.get().getGameField().getMapWidth());
                response.addProperty("height", searchedLobby.get().getGameField().getMapHeight());
                return gson.toJson(response);
            } else {
                response = generateDefaultResponse("failed", "No lobby with this id found");
                response.addProperty("width", 0);
                response.addProperty("height", 0);
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            response.addProperty("width", 0);
            response.addProperty("height", 0);
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/initialMap", consumes = "application/json")
    public @ResponseBody String getInitialMap(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        JsonObject response;
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                return gson.toJson(searchedLobby.get().getGameField().getInitialMap());
            } else {
                response = generateDefaultResponse("failed", "No lobby with this id found");
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/state", consumes = "application/json")
    public @ResponseBody String getGameState(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        JsonObject response;
        if (accountService.isLoggedIn(token)) {
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
            } else {
                response = generateDefaultResponse("received", "No update available or update already received");
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/winner", consumes = "application/json")
    public @ResponseBody String getGameWinner(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        return sessionService.getResultForGame(lobbyId);
    }

    @PostMapping(path = "/players", consumes = "application/json")
    public @ResponseBody String getPlayerColors(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        JsonObject response;
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                return gson.toJson(searchedLobby.get().getPlayerColors());
            } else {
                response = generateDefaultResponse("notFound", "No lobby with this id found");
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            return gson.toJson(response);
        }
    }

    private JsonObject generateDefaultResponse(String status, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", status);
        response.addProperty("message", message);
        return response;
    }
}
