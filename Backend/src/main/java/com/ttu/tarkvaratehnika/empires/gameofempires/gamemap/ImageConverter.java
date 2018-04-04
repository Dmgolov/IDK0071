package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamefield.Coordinates;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageConverter {

    public static List<Coordinates> DEAD_CELLS = new ArrayList<>();

    public static JsonArray convertMapWithRGBtoJSON(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        JsonArray array = new JsonArray();
        String tileName = "";
        String hexColor = "";
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = new Color(image.getRGB(col, row));
                float hsb[] = new float[3];
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                Color.RGBtoHSB(r, g, b, hsb);
                float deg = hsb[0] * 360;
                if (deg >= 30 && deg < 33) {
                    tileName = "Desset";
                    hexColor = "#FA9418";
                } else if (deg >= 80 && deg < 88) {
                    tileName = "Plains";
                    hexColor = "#8DB360";
                } else if (deg >= 130 && deg < 138) {
                    tileName = "Forrest";
                    hexColor = "#056618";
                } else if (deg >= 235 && deg < 241){
                    tileName = "Ocean";
                    hexColor = "#000070";
                    DEAD_CELLS.add(new Coordinates(col, row));
                }
                JsonObject object = new JsonObject();
                object.addProperty("x", col);
                object.addProperty("y", row);
                object.addProperty("color", hexColor);
                object.addProperty("biome", tileName);;
                array.add(object);
            }
        }
        return array;
    }
//    private static List<Coordinates> deadLocation() throws IOException {
//        Path path = Paths.get("C:\\uploadFiles\\gameMap.png");
//        BufferedImage imageForConvert = ImageIO.read(path.toFile());
//        JsonArray array = ImageConverter.convertMapWithRGBtoJSON(imageForConvert);
//        for (int i = 0; i <array.size(); i++){
//            String[] string = array.get(i).toString().split(",");
//            if (array.get(i).toString().contains("Ocean")){
//                deadCells.add(new Coordinates(Integer.parseInt(string[0].replaceAll("[\\D]", "")),Integer.parseInt(string[1].replaceAll("[\\D]", ""))));
//            }
//        }
//        return deadCells;
//    }


}
