package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ttu.tarkvaratehnika.empires.gameofempires.messagekeys.MessageKeys;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Optional;

@RestController
public class UserController {

    private Gson gson = new Gson();

    private AccountService accountService;

    @Autowired
    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(path = "/auth/signup", consumes = "application/json")
    public @ResponseBody String signUp(@RequestBody String data) {
        String name = gson.fromJson(data, JsonObject.class).get("displayName").getAsString();
        String email = gson.fromJson(data, JsonObject.class).get("email").getAsString();
        String pass = gson.fromJson(data, JsonObject.class).get("password").getAsString();
        JsonObject response;
        try {
            accountService.register(name, email, pass);
            response = generateDefaultResponse("success", MessageKeys.NULL);
        } catch (DataIntegrityViolationException | NoSuchAlgorithmException | IllegalArgumentException e) {
            response = generateDefaultResponse("failed", e.getMessage());
        }
        return gson.toJson(response);
    }

    @PostMapping(path = "/auth/signin", consumes = "application/json")
    public @ResponseBody String signIn(@RequestBody String data) throws UnsupportedEncodingException {
        JsonObject response;
        JsonElement jsonEmail = gson.fromJson(data, JsonObject.class).get("email");
        JsonElement jsonPass = gson.fromJson(data, JsonObject.class).get("password");
        if (jsonEmail != null && jsonPass != null) {
            String email = jsonEmail.getAsString();
            String pass = jsonPass.getAsString();
            Optional<String> token = accountService.logIn(email, pass);
            if (token.isPresent()) {
                response = generateDefaultResponse("success", MessageKeys.NULL);
                response.addProperty("token", token.get());
                return gson.toJson(response);
            } else {
                response = generateDefaultResponse("failed", MessageKeys.NO_MATCHING_AUTH_DATA);
                return gson.toJson(response);
            }
        } else {
            response = generateDefaultResponse("failed", MessageKeys.EMPTY_AUTH_DATA);
            return gson.toJson(response);
        }
    }

    @GetMapping(path = "/auth/signout")
    public @ResponseBody String logOut() {
        accountService.logOut();
        return gson.toJson(generateDefaultResponse("success", MessageKeys.NULL));
    }

    @GetMapping(path = "/auth/check")
    public @ResponseBody String checkLogIn(Principal principal) {
        JsonObject response;
        if (principal != null) {
            response = generateDefaultResponse("success", MessageKeys.NULL);
        } else {
            response = generateDefaultResponse("failed", MessageKeys.AUTH_FAIL);
        }
        return gson.toJson(response);
    }

    @GetMapping(path = "/auth/user")
    public @ResponseBody String getUsername(@RequestHeader(name = "Authorization", required = false) String token) {
        Optional<String> username = accountService.getUsernameForToken(token);
        JsonObject response;
        if (username.isPresent()) {
            response = generateDefaultResponse("success", MessageKeys.NULL);
            response.addProperty("username", username.get());
        } else {
            response = generateDefaultResponse("failed", MessageKeys.NO_USERNAME_FOUND);
            response.addProperty("username", "null");
        }
        return gson.toJson(response);
    }

    private JsonObject generateDefaultResponse(String result, String message) {
        JsonObject response = new JsonObject();
        response.addProperty("result", result);
        response.addProperty("message", message);
        return response;
    }
}

