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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/lobby")
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

    @PostMapping(path = "/new", consumes = "application/json")
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

    @GetMapping(path = "/defaultSettings")
    public @ResponseBody String getDefaultOptions() {
        return "{\"nationPoints\":" + SessionSettings.NATION_POINTS + "}";
    }

    @PostMapping(path = "/mode", consumes = "application/json")
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

    @RequestMapping(path = "/connect")
    public @ResponseBody boolean connectToLobby(@RequestParam String username, @RequestParam long lobbyId) {
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findFirst();
        return searchedLobby.isPresent() && searchedLobby.get().enterSession(username).isPresent();
    }

    @RequestMapping(path = "/leave")
    public @ResponseBody boolean leaveLobby(@RequestParam String username, @RequestParam long lobbyId) {
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId).findFirst();
        return searchedLobby.isPresent() && searchedLobby.get().leaveSession(username);
    }

    @PostMapping(path = "/ready", consumes = "application/json")
    public @ResponseBody String setPlayerReadyState(@RequestBody String data) {
        JsonObject json = gson.fromJson(data, JsonObject.class);
        Map<String, Integer> stats = gson
                .fromJson(json.get("nationAttributes").toString(), new TypeToken<Map<String, Integer>>(){}.getType());
        JsonObject jsonObject = gson.fromJson(json.get("player"), JsonObject.class);
        lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == json.get("lobbyId").getAsLong())
                .findFirst().ifPresent(lobby -> lobby.readyCheck(jsonObject.get("name").getAsString(),
                jsonObject.get("isReady").getAsBoolean(), stats));
        return "{\"status\":\"ready\"}";
    }

    @RequestMapping(path = "/check")
    public @ResponseBody String checkPlayerState(@RequestParam long lobbyId) {
        Optional<GameLobby> gameLobby = lobbies.stream().filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findAny();
        return gameLobby.isPresent() ? gson.toJson(gameLobby.get().checkPlayerState()) : "{\"status\":\"notFound\"}";
    }

    @RequestMapping(path = "/all")
    public @ResponseBody String getLobbyNames(@RequestParam String filter) {
        return gson.toJson(lobbies.stream().map(GameLobby::getLobbyName)
                .filter(name -> name.contains(filter))
                .collect(Collectors.toList()));
    }

    @RequestMapping(path = "/start")
    public @ResponseBody boolean startLobby(@RequestParam long lobbyId) {
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findFirst();
        return searchedLobby.isPresent() && searchedLobby.get().startSession();
    }

    @RequestMapping(path = "/state")
    public @ResponseBody String getGameState(@RequestParam long lobbyId) {
        Optional<GameLobby> searchedLobby = lobbies.stream()
                .filter(lobby -> lobby.getLobbyId() == lobbyId).findFirst();
        if (!searchedLobby.isPresent()) {
            return results.getOrDefault(lobbyId, "{\"status\":\"notFound\"}");
        }
        return gson.toJson(searchedLobby.get().getLastUpdate());
    }

    //TODO: would be good, if results would be cleared from map after some time
    public void terminateLobby(GameLobby lobby, String winner) {
        lobbies.remove(lobby);
        results.put(lobby.getLobbyId(), winner);
    }
}
