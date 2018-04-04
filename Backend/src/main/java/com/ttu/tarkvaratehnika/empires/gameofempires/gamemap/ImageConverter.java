package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.InGameObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gameobjects.Terrain;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageConverter {
    private static final String MAP_FOLDER = "C:\\uploadFiles\\";

    public static InGameObject[][] createField(String mapName) throws IOException {
        Path path = Paths.get(MAP_FOLDER + mapName);
        BufferedImage image = ImageIO.read(path.toFile());
        int width = image.getWidth();
        int height = image.getHeight();
        InGameObject[][] field = new InGameObject[height][width];
        System.out.println("IMAGE CONVERTER width " + width);
        System.out.println("IMAGE CONVERTER height " + height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                float hsb[] = new float[3];
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                Color.RGBtoHSB(r, g, b, hsb);
                float deg = hsb[0] * 360;
                if (deg >= 30 && deg < 33) {
                    field[y][x] = Terrain.DESERT;
                } else if (deg >= 80 && deg < 88) {
                    field[y][x] = Terrain.PLAINS;
                } else if (deg >= 130 && deg < 138) {
                    field[y][x] = Terrain.FOREST;
                } else if (deg >= 235 && deg < 241){
                    field[y][x] = Terrain.OCEAN;
                } else {
                    field[y][x] = Terrain.RIVER;
                }
            }
        }
        return field;
    }

}
