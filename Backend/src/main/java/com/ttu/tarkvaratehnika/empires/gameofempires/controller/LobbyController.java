package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.SessionSettings;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class LobbyController {

    private Set<GameLobby> lobbies = new HashSet<>();
    private Map<Long, String> results = new HashMap<>();
    private Gson gson = new Gson();

    private AccountService accountService;
    private TemplateService templateService;

    @Autowired
    public LobbyController(AccountService accountService, TemplateService templateService) {
        this.accountService = accountService;
        this.templateService = templateService;
    }

    @PostMapping(path = "/lobby/new", consumes = "application/json")
    public @ResponseBody String createLobby(@RequestBody String data) {
        String username = gson.fromJson(data, JsonObject.class).get("playerName").getAsString();
        if (accountService.isLoggedIn(username)) {
            GameLobby lobby = new GameLobby(this);
            lobbies.add(lobby);
            lobby.setLobbyName("");
            lobby.enterSession(username);
            return "{\"lobbyId\":" + lobby.getLobbyId() + "}";
        }
        return "{\"lobbyId\":0}";
    }

    @GetMapping(path = "/lobby/defaultSettings")
    public @ResponseBody String getDefaultOptions() {
        return "{\"nationPoints\":" + SessionSettings.NATION_POINTS + "}";
    }

    @PostMapping(path = "/game/mapSettings", consumes = "application/json")
    public @ResponseBody String getMapSettings(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        Optional<GameLobby> searchedLobby = lobbies.stream().filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findFirst();
        if (searchedLobby.isPresent()) {
            return String.format("{\"width\":%d,\"height\":%d}", searchedLobby.get().getGameField().getMapWidth(),
                    searchedLobby.get().getGameField().getMapHeight());
        } else {
            return "{\"width\":0,\"height\":0}";
        }
    }

<<<<<<< HEAD
    @GetMapping(path = "/game/initialMap")
    public @ResponseBody String getInitialMap(@RequestParam long lobbyId) throws IOException {
=======
    @PostMapping(path = "/game/initialMap", consumes = "application/json")
    public @ResponseBody String getInitialMap(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
>>>>>>> master
        Optional<GameLobby> searchedLobby = lobbies.stream().filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findFirst();
        if (searchedLobby.isPresent()) {
            return gson.toJson(searchedLobby.get().getGameField().getInitialMap());
        } else {
            return "{}";
        }
    }

    @PostMapping(path = "/lobby/mode", consumes = "application/json")
    public @ResponseBody String changeMode(@RequestBody String data) {
        String mode = gson.fromJson(data, JsonObject.class).get("mode").getAsString();
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId).findFirst();
        if (searchedLobby.isPresent()) {
            if (mode.equals(SessionSettings.SINGLE_PLAYER)) {
                searchedLobby.get().changeToSinglePlayer();
            } else if (mode.equals(SessionSettings.MULTI_PLAYER)) {
                // TODO: change or remove, when multiplayer will be finished
                searchedLobby.get().setSingleMode(false);
            }
            return "{\"status\":\"changed\"}";
        }
        return "{\"status\":\"failed\"}";
    }

    @PostMapping(path = "/lobby/connect", consumes = "application/json")
    public @ResponseBody String connectToLobby(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findFirst();
        String username = gson.fromJson(data, JsonObject.class).get("playerName").getAsString();
        if (searchedLobby.isPresent() && searchedLobby.get().enterSession(username).isPresent()) {
            String mode = searchedLobby.get().isSingleMode() ? "single" : "multi";
            return "{\"status\":\"success\", \"gameMode\":\"" + mode + "\"}";
        }
        return "{\"status\":\"failed\"}";
    }

    @RequestMapping(path = "/lobby/leave")
    public @ResponseBody boolean leaveLobby(@RequestParam String username, @RequestParam long lobbyId) {
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId).findFirst();
        return searchedLobby.isPresent() && searchedLobby.get().leaveSession(username);
    }

    @PostMapping(path = "/lobby/ready", consumes = "application/json")
    public @ResponseBody String setPlayerReadyState(@RequestBody String data) {
        JsonObject json = gson.fromJson(data, JsonObject.class);
        Map<String, Integer> stats = gson
                .fromJson(json.get("nationAttributes").toString(), new TypeToken<Map<String, Integer>>(){}.getType());
        JsonObject jsonObject = gson.fromJson(json.get("player"), JsonObject.class);
        lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == json.get("lobbyId").getAsLong())
                .findFirst().ifPresent(lobby -> {
            try {
                lobby.readyCheck(jsonObject.get("name").getAsString(),
                        jsonObject.get("isReady").getAsBoolean(), stats);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return "{\"status\":\"ready\"}";
    }

    @PostMapping(path = "/lobby/check", consumes = "application/json")
    public @ResponseBody String checkPlayerState(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        Optional<GameLobby> gameLobby = lobbies.stream().filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findAny();
        return gameLobby.isPresent() ? gson.toJson(gameLobby.get().checkPlayerState()) : "{\"status\":\"notFound\"}";
    }

    @RequestMapping(path = "/lobby/all")
    public @ResponseBody String getLobbyNames(@RequestParam String filter) {
        return gson.toJson(lobbies.stream().map(GameLobby::getLobbyName)
                .filter(name -> name.contains(filter))
                .collect(Collectors.toList()));
    }

    @PostMapping(path = "/game/state", consumes = "application/json")
    public @ResponseBody String getGameState(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId).findFirst();
        if (!searchedLobby.isPresent()) {
            return results.containsKey(lobbyId) ? "{\"status\":\"finished\"}" : "{\"status\":\"notFound\"}";
        }
        int turnNr = gson.fromJson(data, JsonObject.class).get("turnNr").getAsInt();
        String username = gson.fromJson(data, JsonObject.class).get("name").getAsString();
        GameLobby lobby = searchedLobby.get();
        Optional<JsonObject> update = lobby.sendUpdateToUser(username, turnNr);
        if (update.isPresent()) {
            lobby.checkIfEveryoneReceivedUpdate();
            return update.get().toString();
        }
        return "{\"status\":\"received\"}";
    }

    @PostMapping(path = "/game/winner", consumes = "application/json")
    public @ResponseBody String getGameWinner(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        return results.getOrDefault(lobbyId, "{\"winner\":\"notFound\"}");
    }

    @PostMapping(path = "game/players", consumes = "application/json")
    public @ResponseBody String getPlayerColors(@RequestBody String data) {
        long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId).findFirst();
        if (searchedLobby.isPresent()) {
            return gson.toJson(searchedLobby.get().getPlayerColors());
        }
        return "{\"status\":\"notFound\"}";
    }

    //TODO: would be good, if results would be cleared from map after some time
    public void terminateLobby(GameLobby lobby, String winner, String color) {
        lobbies.remove(lobby);
        results.put(lobby.getLobbyId(), "{\"name\":\"" + winner + "\", \"color\":\"" + color + "\"}");
    }
}
