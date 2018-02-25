package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:9000")
@RequestMapping(path = "/lobby")
public class LobbyController {

    private Set<GameLobby> lobbies = new HashSet<>();
    Gson gson = new Gson();

    private AccountService accountService;
    private TemplateService templateService;

    @Autowired
    public LobbyController(AccountService accountService, TemplateService templateService) {
        this.accountService = accountService;
        this.templateService = templateService;
    }

    @RequestMapping(path = "/new")
    public @ResponseBody long createLobby(@RequestParam String username, @RequestParam String lobbyName,
                                          @RequestParam String lobbyPass) {
        if (accountService.isLoggedIn(username)) {
            GameLobby lobby = new GameLobby(this);
            lobbies.add(lobby);
            lobby.setLobbyName(lobbyName);
            lobby.setLobbyPass(lobbyPass);
            lobby.enterSession(username);
            return lobby.getLobbyId();
        }
        return 0;
    }

    @RequestMapping(path = "/connect")
    public @ResponseBody boolean connectToLobby(@RequestParam String username, @RequestParam long lobbyId,
                                  @RequestParam String lobbyPass) {
        Optional<GameLobby> searchedSession = lobbies.stream()
                .filter(session -> session.getLobbyId() == lobbyId && session.getLobbyPass().equals(lobbyPass))
                .findFirst();
        return searchedSession.isPresent() && searchedSession.get().enterSession(username);
    }

    @PostMapping(path = "/ready", consumes = "application/json")
    public @ResponseBody String readyCheck(@RequestBody String data) {
        JsonObject json = gson.fromJson(data, JsonObject.class);
        Map<String, Integer> stats = gson
                .fromJson(json.get("nationAttributes").toString(), new TypeToken<Map<String, Object>>(){}.getType());
        JsonObject jsonObject = gson.fromJson(json.get("player"), JsonObject.class);
        lobbies.stream()
                .filter(session -> session.getLobbyId() == 1)
                .findFirst().ifPresent(session -> session.readyCheck(jsonObject.get("name").toString(),
                jsonObject.get("isReady").getAsBoolean(), stats));
        Map<String, String> map = new HashMap<>();
        map.put("status", "ready");
        return gson.toJson(map);
    }

    @RequestMapping(path = "/check")
    public @ResponseBody String checkPlayerState(@RequestParam long lobbyId) {
        Optional<GameLobby> gameLobby = lobbies.stream().filter(lobby -> lobby.getLobbyId() == lobbyId)
                .findAny();
        return gameLobby.isPresent() ? gson.toJson(gameLobby.get().checkPlayerState()) : null;
    }

    @RequestMapping(path = "/all")
    public @ResponseBody String getLobbyNames(@RequestParam String filter) {
        return gson.toJson(lobbies.stream().map(GameLobby::getLobbyName)
                .filter(name -> name.contains(filter))
                .collect(Collectors.toList()));
    }

    @RequestMapping(path = "/start")
    public @ResponseBody boolean startLobby(@RequestParam long lobbyId) {
        Optional<GameLobby> searchedSession = lobbies.stream()
                .filter(session -> session.getLobbyId() == lobbyId)
                .findFirst();
        return searchedSession.isPresent() && searchedSession.get().startSession();
    }

    @RequestMapping(path = "/state")
    public @ResponseBody String sendSessionResult(@RequestParam long lobbyId) {
        return null;
    }
}
