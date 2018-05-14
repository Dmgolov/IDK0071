package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageConverter {

    public static final String MAP_FOLDER = "maps\\";

    public static InGameObject[][] createField(String mapName) throws IOException {
        Path path = Paths.get(MAP_FOLDER + mapName);
        BufferedImage image = ImageIO.read(path.toFile());
        int width = image.getWidth();
        int height = image.getHeight();
        InGameObject[][] field = new InGameObject[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                String RGB = String.format("%d %d %d", color.getRed(), color.getGreen(), color.getBlue());
                switch (RGB) {
                    case "141 179 96":
                        field[y][x] = Terrain.PLAINS;
                        break;
                    case "5 102 33":
                        field[y][x] = Terrain.FOREST;
                        break;
                    case "250 148 24":
                        field[y][x] = Terrain.DESERT;
                        break;
                    case "0 0 112":
                        field[y][x] = Terrain.OCEAN;
                        break;
                    case "0 0 255":
                        field[y][x] = Terrain.RIVER;
                        break;
                    default:
                        field[y][x] = Terrain.RIVER;
                }
            }
        }
        return field;
    }

}
