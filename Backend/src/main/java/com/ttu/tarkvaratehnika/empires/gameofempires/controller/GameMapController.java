package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.JsonArray;
import com.ttu.tarkvaratehnika.empires.gameofempires.gamemap.ImageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class GameMapController {

    private static String UPLOADED_FOLDER = "C:/uploadFiles/";

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            return "PLEASE, DO NOT UPLOAD EMPTY FILE";
        }

        try {

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            BufferedImage imageForConvert = ImageIO.read(path.toFile());
            JsonArray convertedMap = ImageConverter.convertMapWithRGBtoJSON(imageForConvert);
            byte[] mapToBytes = convertedMap.toString().getBytes();
            Files.write(Paths.get(UPLOADED_FOLDER + "test.json"), mapToBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "UPLOADED";
    }
}