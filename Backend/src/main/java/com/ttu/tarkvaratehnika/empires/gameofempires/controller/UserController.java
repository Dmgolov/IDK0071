package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private Gson gson = new Gson();

    private AccountService accountService;
    private TemplateService templateService;

    @Autowired
    public UserController(AccountService accountService, TemplateService templateService) {
        this.accountService = accountService;
        this.templateService = templateService;
    }

    @PostMapping(path = "/auth/signup", consumes = "application/json")
    public @ResponseBody String signUp(@RequestBody String data) {
        String name = gson.fromJson(data, JsonObject.class).get("displayName").getAsString();
        String email = gson.fromJson(data, JsonObject.class).get("email").getAsString();
        String pass = gson.fromJson(data, JsonObject.class).get("password").getAsString();
        try {
            String token = accountService.register(name, email, pass);
            if (token != null) {
                return "{\"token\":\"" + token + "\", \"result\":\"success\"}";
            }
        } catch (DataIntegrityViolationException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "{\"token\":null,\"result\":\"failed\"}";
    }

    @PostMapping(path = "/auth/signin", consumes = "application/json")
    public @ResponseBody String signIn(@RequestBody String data) throws UnsupportedEncodingException {
        String email = gson.fromJson(data, JsonObject.class).get("email").getAsString();
        String pass = gson.fromJson(data, JsonObject.class).get("password").getAsString();
        String token = accountService.logIn(email, pass);
        if (token != null) {
            return "{\"token\":\"" + token + "\", \"result\":\"success\"}";
        }
        return "{\"token\":null,\"result\":\"failed\"}";
    }

    //TODO: change depending on how templates will be implemented
    @RequestMapping(path = "/template/new")
    public @ResponseBody String uploadTemplate(@RequestParam String username, @RequestParam String pass,
                                               @RequestParam String template) {
        if (accountService.isLoggedIn(username)) {
            boolean result = templateService.addNewTemplate(username, template);
            return result ? "Saved template" : "Failed to save template";
        } else {
            return "Log in first";
        }
    }

    @RequestMapping(path = "/template/browse")
    public @ResponseBody List<String> findTemplates(String filter) {
        return templateService.findTemplates(filter);
    }

    @RequestMapping(path = "/template/load")
    public @ResponseBody Map<String, Integer> loadTemplate(@RequestParam String templateName) {
        return templateService.findTemplate(templateName);
    }
}
