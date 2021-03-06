package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.messagekeys.MessageKeys;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.GameMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class GameMapController {

    private GameMapService gameMapService;

    private Gson gson = new Gson();

    @Autowired
    public GameMapController(GameMapService gameMapService) {
        this.gameMapService = gameMapService;
    }

    @RequestMapping(path = "/map/image", consumes = "application/json", method = RequestMethod.POST, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] sendImageToUsers(@RequestBody String data) {
        String mapName = gson.fromJson(data, JsonObject.class).get("mapName").getAsString();
        try {
            return gameMapService.findMap(mapName);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return new byte[]{};
        }
    }

    @PostMapping(path = "/map/upload", consumes = "multipart/form-data")
    public String singleFileUpload(@RequestParam("mapImage") MultipartFile file, @RequestParam(value = "username") String username,
                                   @RequestParam(value = "mapName", required = false) String mapName) {
        JsonObject response = new JsonObject();
        try {
            String filename = file.getOriginalFilename();
            if (filename.equals("EMPTY_FILE") || !filename.contains(".")) throw new IllegalArgumentException("No file found");
            mapName = (mapName == null || mapName.isEmpty()) ? filename.substring(0, filename.indexOf(".")) : mapName;
            gameMapService.saveFile(file, mapName, username);
            response.addProperty("status", "success");
            response.addProperty("message", MessageKeys.UPLOAD_SUCCESS);
        } catch (IOException | IllegalArgumentException e) {
            response.addProperty("status", "failed");
            response.addProperty("message", e.getMessage());
        }
        return gson.toJson(response);
    }

    @PostMapping(value = "/map/browse", consumes = {"application/json"})
    public @ResponseBody
    String getAvailableMapsFromMaps(@RequestBody String data) {
        JsonElement filter = gson.fromJson(data, JsonObject.class).get("username");
        String name = filter == null ? "" : filter.getAsString();
        return gson.toJson(getMaps(name));
    }

    @PostMapping(value = "/lobby/browse", consumes = {"application/json"})
    public @ResponseBody
    String getAvailableMapsFromLobby(@RequestBody String data) {
        JsonElement filter = gson.fromJson(data, JsonObject.class).get("username");
        String name = filter == null ? "" : filter.getAsString();
        return gson.toJson(getMaps(name));
    }

    private List<String> getMaps(String filter) {
        List<String> names = new ArrayList<>();
        Iterable<GameMap> maps = gameMapService.getMaps(filter);
        for (GameMap map : maps) {
            names.add(map.getName());
        }
        return names;
    }
}