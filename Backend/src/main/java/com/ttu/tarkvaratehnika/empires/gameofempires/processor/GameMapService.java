package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameMapRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class GameMapService {

    public static final String MAP_PATH = "maps/";
    public static final String FILE_EXTENSION = ".png";

    private GameMapRepository gameMapRepository;

    @Autowired
    public GameMapService(GameMapRepository gameMapRepository) {
        this.gameMapRepository = gameMapRepository;
    }

    public Iterable<GameMap> getMaps() {
        return gameMapRepository.findAll();
    }

    public byte[] findMap(String mapName) throws IOException, IllegalArgumentException {
        Optional<GameMap> requested = gameMapRepository.getGameMapByName(mapName);
        if (requested.isPresent()) {
            GameMap gameMap = requested.get();
            String filename = gameMap.getName() + FILE_EXTENSION;
            InputStream in = new FileInputStream(MAP_PATH + filename);
            return IOUtils.toByteArray(in);
        } else {
            throw new IllegalArgumentException("No such map found");
        }
    }

    public void saveFile(MultipartFile file, String mapName, String username) throws IOException {
        File local = new File("maps");
        if  (!local.exists()) {
            if (!local.mkdir()) throw new IOException("Failed to create directory");
        }
        byte[] bytes = file.getBytes();
        Date creationDate = Date.valueOf(LocalDate.now());
        String filename = mapName + FILE_EXTENSION;
        Path path = Paths.get(MAP_PATH + filename);
        Files.write(path, bytes);
        saveToDatabase(mapName, username, creationDate);
    }

    private void saveToDatabase(String mapName, String username, Date creationDate) {
        GameMap map = new GameMap();
        map.setAuthor(username);
        map.setName(mapName);
        map.setCreationDate(creationDate);
        gameMapRepository.save(map);
    }
}
