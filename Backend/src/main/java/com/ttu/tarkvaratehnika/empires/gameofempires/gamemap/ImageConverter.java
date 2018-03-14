package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageConverter {
//    public static void main(String[] args) throws IOException {
//
//        BufferedImage hugeImage = ImageIO.read(new FileInputStream("src/main/java/com/ttu/tarkvaratehnika/empires/gameofempires/gamemap/map145x145.png"));
//
//        System.out.println("Start converting map: ");
//        JsonArray result = convertTo2DUsingGetRGB(hugeImage);
//        System.out.println(result);
//    }

    public static JsonArray convertMapWithRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        JsonArray array = new JsonArray();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = new Color(image.getRGB(col,row));
                JsonObject object = new JsonObject();
                object.addProperty("x", row);
                object.addProperty("y", col);
                object.addProperty("color", String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
                array.add(object);
            }
        }
        return array;
    }

}
