package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.messagekeys.MessageKeys;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(path = "/game")
public class SessionController {

    private Gson gson = new Gson();

    private SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping(path = "/mapSettings", consumes = "application/json")
    public @ResponseBody
    String getMapSettings(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                response = generateDefaultResponse("success", MessageKeys.NULL);
                response.addProperty("width", searchedLobby.get().getGameField().getMapWidth());
                response.addProperty("height", searchedLobby.get().getGameField().getMapHeight());
                return gson.toJson(response);
            } else {
                response = generateDefaultResponse("failed", MessageKeys.LOBBY_NOT_FOUND);
                response.addProperty("width", 0);
                response.addProperty("height", 0);
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", MessageKeys.AUTH_FAIL);
            response.addProperty("width", 0);
            response.addProperty("height", 0);
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/initialMap", consumes = "application/json")
    public @ResponseBody String getInitialMap(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                return gson.toJson(searchedLobby.get().getGameField().getInitialMap());
            } else {
                response = generateDefaultResponse("failed", MessageKeys.LOBBY_NOT_FOUND);
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", MessageKeys.AUTH_FAIL);
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/state", consumes = "application/json")
    public @ResponseBody String getGameState(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (!searchedLobby.isPresent()) {
                if (sessionService.isGameFinished(lobbyId)) {
                    response = generateDefaultResponse("finished", MessageKeys.NULL);
                    return gson.toJson(response);
                } else {
                    response = generateDefaultResponse("notFound", MessageKeys.NULL);
                    return gson.toJson(response);
                }
            }
            int turnNr = gson.fromJson(data, JsonObject.class).get("turnNr").getAsInt();
            String username = gson.fromJson(data, JsonObject.class).get("name").getAsString();
            GameLobby lobby = searchedLobby.get();
            Optional<JsonObject> update = lobby.sendUpdateToUser(username, turnNr);
            if (update.isPresent()) {
                lobby.checkIfEveryoneReceivedUpdate();
                return update.get().toString();
            } else {
                response = generateDefaultResponse("received", MessageKeys.NO_UPDATE);
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", MessageKeys.AUTH_FAIL);
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/winner", consumes = "application/json")
    public @ResponseBody String getGameWinner(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        return sessionService.getResultForGame(lobbyId);
    }

    @PostMapping(path = "/players", consumes = "application/json")
    public @ResponseBody String getPlayerColors(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                return gson.toJson(searchedLobby.get().getPlayerColors());
            } else {
                response = generateDefaultResponse("notFound", MessageKeys.LOBBY_NOT_FOUND);
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", MessageKeys.AUTH_FAIL);
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
