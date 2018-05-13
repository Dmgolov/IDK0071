package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.SessionSettings;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.SessionService;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping(path = "/lobby")
public class LobbyController {

    private Gson gson = new Gson();
    private static long lastLobbyId;

    private SessionService sessionService;
    private AccountService accountService;
    private GameRepository gameRepository;

    @Autowired
    public LobbyController(AccountService accountService, SessionService sessionService, GameRepository gameRepository) {
        this.accountService = accountService;
        this.sessionService = sessionService;
        this.gameRepository = gameRepository;
        lastLobbyId = gameRepository.getMaxId().isPresent() ? gameRepository.getMaxId().get() : 0;
    }

    @PostMapping(path = "/new", consumes = "application/json")
    public @ResponseBody String createLobby(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            String username = gson.fromJson(data, JsonObject.class).get("playerName").getAsString();
            GameLobby lobby = new GameLobby(sessionService, ++lastLobbyId);
            lobby.enterSession(username);
            sessionService.addLobby(lobby);
            return "{\"lobbyId\":" + lobby.getLobbyId() + "}";
        }
        return "{\"lobbyId\":0}";
    }

    @GetMapping(path = "/defaultSettings")
    public @ResponseBody String getDefaultOptions(@RequestHeader(name = "Authorization", required = false) String token) {
        if (accountService.isLoggedIn(token)) {
            return "{\"nationPoints\":" + SessionSettings.NATION_POINTS + "}";
        }
        return "{\"nationPoints\":0}";
    }

    @PostMapping(path = "/mode", consumes = "application/json")
    public @ResponseBody String changeMode(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            String mode = gson.fromJson(data, JsonObject.class).get("mode").getAsString();
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                if (mode.equals(SessionSettings.SINGLE_PLAYER)) {
                    searchedLobby.get().changeToSinglePlayer();
                } else if (mode.equals(SessionSettings.MULTI_PLAYER)) {
                    searchedLobby.get().setSingleMode(false);
                }
                return "{\"status\":\"changed\"}";
            }
        }
        return "{\"status\":\"failed\"}";
    }

    @PostMapping(path = "/connect", consumes = "application/json")
    public @ResponseBody String connectToLobby(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            String username = gson.fromJson(data, JsonObject.class).get("playerName").getAsString();
            if (searchedLobby.isPresent() && searchedLobby.get().enterSession(username).isPresent()) {
                String mode = searchedLobby.get().isSingleMode() ? "single" : "multi";
                return "{\"status\":\"success\", \"gameMode\":\"" + mode + "\"}";
            }
        }
        return "{\"status\":\"failed\"}";
    }

    @RequestMapping(path = "/leave")
    public @ResponseBody boolean leaveLobby(@RequestHeader(name = "Authorization", required = false) String token, @RequestParam String username, @RequestParam long lobbyId) {
        if (accountService.isLoggedIn(token)) {
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            return searchedLobby.isPresent() && searchedLobby.get().leaveSession(username);
        }
        return false;
    }

    @PostMapping(path = "/ready", consumes = "application/json")
    public @ResponseBody String setPlayerReadyState(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            JsonObject json = gson.fromJson(data, JsonObject.class);
            Map<String, Integer> stats = gson
                    .fromJson(json.get("nationAttributes").toString(), new TypeToken<Map<String, Integer>>(){}.getType());
            JsonObject jsonObject = gson.fromJson(json.get("player"), JsonObject.class);
            long lobbyId = json.get("lobbyId").getAsLong();
            String username = jsonObject.get("name").getAsString();
            boolean isReady = jsonObject.get("isReady").getAsBoolean();
            String mapName = gson.fromJson(data, JsonObject.class).get("mapName").getAsString();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                try {
                    searchedLobby.get().readyCheck(username, isReady, stats, mapName);
                } catch (IOException e) {
                    return "{\"status\":\"failed\", \"error\":\"" + e.getMessage() + "\"}";
                }
            }
            return "{\"status\":\"ready\"}";
        }
        return "{\"status\":\"failed\"}";
    }

    @PostMapping(path = "/check", consumes = "application/json")
    public @ResponseBody String checkPlayerState(@RequestHeader(name = "Authorization", required = false) String token, @RequestBody String data) {
        if (accountService.isLoggedIn(token)) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> gameLobby = sessionService.findLobbyById(lobbyId);
            return gameLobby.isPresent() ? gson.toJson(gameLobby.get().checkPlayerState()) : "{\"status\":\"notFound\"}";
        }
        return "{\"status\":\"notFound\"}";
    }

    @ResponseBody
    @RequestMapping(path = "/lobby/image", consumes = "application/json", method = RequestMethod.POST, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] sendImageToLobby(@RequestBody String imageName) throws IOException {
        System.out.println(imageName);
        String selectedMap = gson.fromJson(imageName, JsonObject.class).get("imageName").getAsString();
        InputStream in = new FileInputStream("uploadFiles/" + selectedMap);
        return IOUtils.toByteArray(in);
    }
}
