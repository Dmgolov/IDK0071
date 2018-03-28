package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageConverter {

    public static JsonArray convertMapWithRGBtoJSON(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        JsonArray array = new JsonArray();
        String tileColor = "";
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = new Color(image.getRGB(col,row));
                float hsb[] = new float[3];
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                Color.RGBtoHSB(r, g, b, hsb);
                float deg = hsb[0] * 360;
                if (deg >= 45 && deg < 75){
                    tileColor = "Dessert";
                } else if (deg >= 75 && deg < 160) {
                    tileColor = "Land";
                } else if (deg >= 160 && deg < 260) {
                    tileColor = "Water";
                }
                JsonObject object = new JsonObject();
                object.addProperty("x", row);
                object.addProperty("y", col);
                object.addProperty("color", tileColor);
                array.add(object);
            }
        }
        return array;
    }

}
