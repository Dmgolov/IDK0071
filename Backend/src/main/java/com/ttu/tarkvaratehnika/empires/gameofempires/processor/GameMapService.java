package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.GameMap;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.ImageConverter;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamesession.SessionSettings;
import com.ttu.tarkvaratehnika.empires.gameofempires.messagekeys.MessageKeys;
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

    private GameMapRepository gameMapRepository;

    @Autowired
    public GameMapService(GameMapRepository gameMapRepository) {
        this.gameMapRepository = gameMapRepository;
    }

    public Iterable<GameMap> getMaps() {
        return gameMapRepository.findAll();
    }

    public Iterable<GameMap> getMaps(String filter) {
        return gameMapRepository.getGameMaps(filter);
    }

    public byte[] findMap(String mapName) throws IOException, IllegalArgumentException {
        Optional<GameMap> requested = gameMapRepository.getGameMapByName(mapName.substring(0, mapName.indexOf('.')));
        if (requested.isPresent()) {
            GameMap gameMap = requested.get();
            String filename = gameMap.getName() + gameMap.getFileExtension();
            InputStream in = new FileInputStream(ImageConverter.MAP_FOLDER + filename);
            return IOUtils.toByteArray(in);
        } else {
            throw new IllegalArgumentException(MessageKeys.NO_MAP_FOUND);
        }
    }

    public void saveFile(MultipartFile file, String mapName, String username) throws IOException, IllegalArgumentException {
        File local = new File("maps");
        if  (!local.exists()) {
            if (!local.mkdir()) throw new IOException(MessageKeys.NEW_DIR_CREATION_FAIL);
        }
        byte[] bytes = file.getBytes();
        Date creationDate = Date.valueOf(LocalDate.now());
        String filename = file.getOriginalFilename();
        saveToDatabase(mapName, username, creationDate, filename.substring(filename.indexOf('.')));
        Path path = Paths.get(ImageConverter.MAP_FOLDER + mapName + filename.substring(filename.indexOf('.')));
        Files.write(path, bytes);
    }

    private void saveToDatabase(String mapName, String username, Date creationDate, String fileExtension) {
        if (gameMapRepository.getGameMapByName(mapName).isPresent()) {
            throw new IllegalArgumentException(MessageKeys.DUPLICATE_MAP_NAME);
        }
        GameMap map = new GameMap();
        map.setAuthor(username);
        map.setName(mapName);
        map.setCreationDate(creationDate);
        map.setFileExtension(fileExtension);
        gameMapRepository.save(map);
    }

    public String getGameMap(String mapName) {
        Optional<GameMap> map = gameMapRepository.getGameMapByName(mapName);
        if (map.isPresent()) {
            return map.get().getName() + map.get().getFileExtension();
        } else {
            return SessionSettings.DEFAULT_MAP;
        }
    }
}
