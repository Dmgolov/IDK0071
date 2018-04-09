package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.io.IOUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
public class GameMapController {

    private static String UPLOADED_FOLDER = "uploadedFiles/";
    private Gson gson = new Gson();
    private ServletContext servletContext;

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping(path = "/map/upload", consumes = "multipart/form-data")
    public String singleFileUpload(@RequestParam("mapImage") MultipartFile file) {
        if (file.isEmpty()) {
            return "PLEASE, DO NOT UPLOAD EMPTY FILE";
        }
        try {
            new File(UPLOADED_FOLDER).mkdirs();
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "UPLOADED";
    }

    @PostMapping(value = "/map/browse", consumes = {"application/json"})
    public @ResponseBody
    String browseMapPage() throws IOException{
        System.out.println("MAPS");
        InputStream image = getClass().getResourceAsStream("/uploadFiles/gameMap5.png");
        List<String> fileList = new LinkedList<>();
        File directory = new File("uploadFiles/");
        Optional<File[]> files = Optional.ofNullable(directory.listFiles());
        if (files.isPresent()){
            File[] filesArray = files.get();
            String[] filesName = new String[filesArray.length];
            for (int i = 0; i < filesArray.length; i++){
                filesName[i] = filesArray[i].getName();
            }
            fileList = Arrays.asList(filesName);
        }
        return gson.toJson(fileList);
    }

    @ResponseBody
    @RequestMapping(value = "/map/image", method = RequestMethod.POST, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] sendImageToUsers() throws IOException {
        InputStream in = new FileInputStream("uploadFiles/gameMap5.png");
        return IOUtils.toByteArray(in);
    }

}