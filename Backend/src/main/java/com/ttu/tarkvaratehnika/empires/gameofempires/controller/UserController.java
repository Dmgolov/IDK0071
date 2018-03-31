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
import java.util.Optional;

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
            accountService.register(name, email, pass);
            return "{\"result\":\"success\"}";
        } catch (DataIntegrityViolationException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "{\"result\":\"failed\"}";
    }

    @PostMapping(path = "/auth/signin", consumes = "application/json")
    public @ResponseBody String signIn(@RequestBody String data) throws UnsupportedEncodingException {
        String email = gson.fromJson(data, JsonObject.class).get("email").getAsString();
        String pass = gson.fromJson(data, JsonObject.class).get("password").getAsString();
        return accountService.logIn(email, pass);
    }

    @GetMapping(path = "/auth/signout")
    public @ResponseBody String logOut(@RequestHeader(name = "Authorization", required = false) String token) {
        accountService.logOut(token);
        return "{\"result\":\"success\"}";
    }

    @GetMapping(path = "/auth/check")
    public @ResponseBody String checkLogIn(@RequestHeader(name = "Authorization", required = false) String token) {
        if (accountService.isLoggedIn(token)) {
            return "{\"result\":\"success\"}";
        }
        return "{\"result\":\"failed\"}";
    }

    @GetMapping(path = "/auth/user")
    public @ResponseBody String getUsername(@RequestHeader(name = "Authorization", required = false) String token) {
        Optional<String> username = accountService.getUsernameForToken(token);
        if (username.isPresent()) return "{\"username\":\"" + username.get() + "\", \"result\":\"success\"}";
        return "{\"username\":null,\"result\":\"failed\"}";
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
