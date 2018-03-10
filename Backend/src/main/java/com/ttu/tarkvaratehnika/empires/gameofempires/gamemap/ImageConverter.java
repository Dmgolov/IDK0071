package com.ttu.tarkvaratehnika.empires.gameofempires.gamemap;

import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageConverter {
    public static void main(String[] args) throws IOException {

        BufferedImage hugeImage = ImageIO.read(new FileInputStream("src/main/java/com/ttu/tarkvaratehnika/empires/gameofempires/gamemap/map145x145.png"));

        System.out.println("Testing convertTo2DUsingGetRGB:");
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
            int[][] result = convertTo2DUsingGetRGB(hugeImage);
            long endTime = System.nanoTime();
        }

        System.out.println("");

        System.out.println("Testing convertTo2DWithoutUsingGetRGB:");
        for (int i = 0; i < 10; i++) {
            long startTime = System.nanoTime();
          //  int[][] result = convertTo2DWithoutUsingGetRGB(hugeImage);
            long endTime = System.nanoTime();
        }
    }

    private static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];
        JsonObject object = new JsonObject();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getRGB(col, row);
            }
        }
        return result;
    }

}
