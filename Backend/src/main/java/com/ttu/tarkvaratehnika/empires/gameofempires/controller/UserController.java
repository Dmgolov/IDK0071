package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
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

    @PostMapping(path = "/register", consumes = "application/json")
    public @ResponseBody String signUp(@RequestBody String data) {
        try {
            return "{\"token\":\"" + accountService.register(gson.fromJson(data, JsonObject.class).get("username").getAsString(),
                    gson.fromJson(data, JsonObject.class).get("password").getAsString()) + "\", \"result\":\"success\"}";
        } catch (DataIntegrityViolationException | GeneralSecurityException e) {
            return "{\"token\":null,\"result\":\"" + e.getMessage() + "\"}";
        }
    }

    @PostMapping(path = "/login", consumes = "application/json")
    public @ResponseBody String signIn(@RequestBody String data) {
        return "{\"token\":\"" + accountService.logIn(gson.fromJson(data, JsonObject.class).get("username").getAsString(),
                gson.fromJson(data, JsonObject.class).get("password").getAsString()) + "\"}";
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
