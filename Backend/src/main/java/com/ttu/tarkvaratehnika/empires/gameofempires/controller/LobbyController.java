package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.GameLobby;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.Properties;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.SessionSettings;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.SessionService;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping(path = "/lobby")
public class LobbyController {

    private Gson gson = new Gson();
    private static long lastLobbyId;

    private SessionService sessionService;

    @Autowired
    public LobbyController(SessionService sessionService, GameRepository gameRepository) {
        this.sessionService = sessionService;
        lastLobbyId = gameRepository.getMaxId().isPresent() ? gameRepository.getMaxId().get() : 0;
    }

    @PostMapping(path = "/new", consumes = "application/json")
    public @ResponseBody String createLobby(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            String username = gson.fromJson(data, JsonObject.class).get("playerName").getAsString();
            GameLobby lobby = new GameLobby(sessionService, ++lastLobbyId);
            lobby.enterSession(username);
            sessionService.addLobby(lobby);
            response = generateDefaultResponse("success", "null");
            response.addProperty("lobbyId", lobby.getLobbyId());
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            response.addProperty("lobbyId", 0);
        }
        return gson.toJson(response);
    }

    @GetMapping(path = "/defaultSettings")
    public @ResponseBody String getDefaultOptions(Principal principal) {
        if (principal != null) {
            return gson.toJson(new Properties());
        } else {
            JsonObject response = generateDefaultResponse("failed", "Authorization failed");
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/getCustomSettings", consumes = "application/json")
    public @ResponseBody String getCustomSettings(Principal principal, @RequestBody String data) {
        if (principal != null) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Properties properties = sessionService.getLobbySettings(lobbyId);
            return gson.toJson(properties);
        } else {
            JsonObject response = generateDefaultResponse("failed", "Authorization failed");
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/setCustomSettings", consumes = "application/json")
    public @ResponseBody String setCustomSettings(Principal principal, @RequestBody Properties properties) {
        JsonObject response;
        if (principal != null) {
            try {
                sessionService.setLobbySettings(properties);
                response = generateDefaultResponse("success", "null");
                return gson.toJson(response);
            } catch (IllegalArgumentException e) {
                response = generateDefaultResponse("failed", e.getMessage());
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authorization failed");
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/mode", consumes = "application/json")
    public @ResponseBody String changeMode(Principal principal, @RequestBody String data) {
        JsonObject response = new JsonObject();
        if (principal != null) {
            String mode = gson.fromJson(data, JsonObject.class).get("mode").getAsString();
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                if (mode.equals(SessionSettings.SINGLE_PLAYER)) {
                    searchedLobby.get().setSingleMode(true);
                } else if (mode.equals(SessionSettings.MULTI_PLAYER)) {
                    searchedLobby.get().setSingleMode(false);
                }
                response.addProperty("status", "changed");
                response.addProperty("message", "null");
                return gson.toJson(response);
            } else {
                response.addProperty("status", "failed");
                response.addProperty("message", "No lobby with this id found");
                return gson.toJson(response);
            }
        } else {
            response.addProperty("status", "failed");
            response.addProperty("message", "Authentication failed");
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/connect", consumes = "application/json")
    public @ResponseBody String connectToLobby(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            String username = gson.fromJson(data, JsonObject.class).get("playerName").getAsString();
            if (searchedLobby.isPresent() && searchedLobby.get().enterSession(username).isPresent()) {
                String mode = searchedLobby.get().isSingleMode() ? "single" : "multi";
                response = generateDefaultResponse("success", "null");
                response.addProperty("gameMode", mode);
                return gson.toJson(response);
            } else {
                response = generateDefaultResponse("failed", "No lobby with this id found");
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            return gson.toJson(response);
        }
    }

    @RequestMapping(path = "/leave")
    public @ResponseBody boolean leaveLobby(Principal principal, @RequestParam String username, @RequestParam long lobbyId) {
        if (principal != null) {
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            return searchedLobby.isPresent() && searchedLobby.get().leaveSession(username);
        }
        return false;
    }

    @PostMapping(path = "/ready", consumes = "application/json")
    public @ResponseBody String setPlayerReadyState(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            JsonObject json = gson.fromJson(data, JsonObject.class);
            Map<String, Integer> stats = gson
                    .fromJson(json.get("nationAttributes").toString(), new TypeToken<Map<String, Integer>>(){}.getType());
            JsonObject jsonObject = gson.fromJson(json.get("player"), JsonObject.class);
            long lobbyId = json.get("lobbyId").getAsLong();
            String username = jsonObject.get("name").getAsString();
            boolean isReady = jsonObject.get("isReady").getAsBoolean();
            Optional<GameLobby> searchedLobby = sessionService.findLobbyById(lobbyId);
            if (searchedLobby.isPresent()) {
                try {
                    searchedLobby.get().readyCheck(username, isReady, stats);
                    response = generateDefaultResponse("ready", "null");
                    return gson.toJson(response);
                } catch (IOException e) {
                    response = generateDefaultResponse("failed", e.getMessage());
                    response.addProperty("error", e.getMessage());
                    return gson.toJson(response);
                }
            } else {
                response = generateDefaultResponse("failed", "No lobby with this id found");
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            return gson.toJson(response);
        }
    }

    @PostMapping(path = "/check", consumes = "application/json")
    public @ResponseBody String checkPlayerState(Principal principal, @RequestBody String data) {
        JsonObject response;
        if (principal != null) {
            long lobbyId = gson.fromJson(data, JsonObject.class).get("lobbyId").getAsLong();
            Optional<GameLobby> gameLobby = sessionService.findLobbyById(lobbyId);
            if (gameLobby.isPresent()) {
                return gson.toJson(gameLobby.get().checkPlayerState());
            } else {
                response = generateDefaultResponse("notFound", "No lobby with this id found");
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", "Authentication failed");
            return gson.toJson(response);
        }
    }

    @ResponseBody
    @RequestMapping(path = "/lobby/image", consumes = "application/json", method = RequestMethod.POST, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] sendImageToLobby(@RequestBody String imageName) throws IOException {
        System.out.println(imageName);
        String selectedMap = gson.fromJson(imageName, JsonObject.class).get("imageName").getAsString();
        InputStream in = new FileInputStream("maps/" + selectedMap);
        return IOUtils.toByteArray(in);
    }

    private JsonObject generateDefaultResponse(String status, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("status", status);
        response.addProperty("message", message);
        return response;
    }
}
