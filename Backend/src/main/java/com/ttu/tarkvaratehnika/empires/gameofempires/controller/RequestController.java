package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountProcessor;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateProcessor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

//TODO: implement
//Handles incoming requests and calls required classes/methods
@RestController
public class RequestController {

    private Gson gson = new Gson();

    private SessionController sessionController = new SessionController();
    private AccountProcessor accountProcessor = new AccountProcessor();
    private TemplateProcessor templateProcessor = new TemplateProcessor();

    @RequestMapping(path = "/register")
    public @ResponseBody String signUp(@RequestParam String username, @RequestParam String pass) {
        boolean result = accountProcessor.register(username, pass);
        return result ? "Registered" : "Failed to register";
    }

    @RequestMapping(path = "/login")
    public @ResponseBody String signIn(@RequestParam String username, @RequestParam String pass) {
        boolean result = accountProcessor.logIn(username, pass);
        return result ? username : "";
    }

    //TODO: later change pass to some kind of token
    @CrossOrigin(origins = "http://localhost:9000")
    @RequestMapping(path = "/lobby/new")
    public @ResponseBody long createNewLobby(@RequestParam(required = false) String username,
                                 @RequestParam String lobbyName, @RequestParam(required = false) String lobbyPass) {
        if (accountProcessor.isLoggedIn(username)) {
            return sessionController.createLobby(username, lobbyName, "");
        } else {
            return 0;
        }
    }

    @RequestMapping(path = "/lobby/all")
    public @ResponseBody String getActiveLobbies(@RequestParam String filter) {
        return gson.toJson(sessionController.getLobbyNames(filter));
    }

    @CrossOrigin(origins = "http://localhost:9000")
    @RequestMapping(path = "/lobby/connect")
    public @ResponseBody String connectToLobby(@RequestParam(required = false) String username,
                                               @RequestParam long lobbyId, @RequestParam(required = false) String lobbyPass) {
        if (accountProcessor.isLoggedIn(username)) {
            boolean result = sessionController.connectToLobby(username, lobbyId, "");
            return result ? "Connected" : "Failed to connect";
        } else {
            return "Log in first";
        }
    }

    @CrossOrigin(origins = "http://localhost:9000")
    @PostMapping(path = "/lobby/ready")
    public @ResponseBody String readyCheck(HttpServletRequest request) {
        Map<String, Integer> stats = gson
                .fromJson(request.getParameter("nationAttributes"), new TypeToken<Map<String, Object>>(){}.getType());
        JsonObject jsonObject = gson.fromJson(request.getParameter("player"), JsonObject.class);
        sessionController.readyCheck(jsonObject.get("name").toString(), 1,
                jsonObject.get("isReady").getAsBoolean(), stats);
        return "Ready";
    }

    //TODO: Return json of players and their state
    @CrossOrigin(origins = "http://localhost:9000")
    @RequestMapping(path = "/lobby/check")
    public @ResponseBody String checkPlayerState(@RequestParam long lobbyId) {
        return gson.toJson(sessionController.checkPlayerState(lobbyId));
    }

    @RequestMapping(path = "/lobby/start")
    public @ResponseBody String startGame(@RequestParam long lobbyId) {
        boolean result = sessionController.startLobby(lobbyId);
        return result ? "Started" : "Failed to start";
    }

    //TODO: change depending on how templates will be implemented
    @RequestMapping(path = "/template/new")
    public @ResponseBody String uploadTemplate(@RequestParam String username, @RequestParam String pass,
                                               @RequestParam String template) {
        if (accountProcessor.isLoggedIn(username)) {
            boolean result = templateProcessor.addNewTemplate(username, template);
            return result ? "Saved template" : "Failed to save template";
        } else {
            return "Log in first";
        }
    }

    @RequestMapping(path = "/template/browse")
    public @ResponseBody List<String> findTemplates(String filter) {
        return templateProcessor.findTemplates(filter);
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public AccountProcessor getAccountProcessor() {
        return accountProcessor;
    }

    public void setAccountProcessor(AccountProcessor accountProcessor) {
        this.accountProcessor = accountProcessor;
    }

    public TemplateProcessor getTemplateProcessor() {
        return templateProcessor;
    }

    public void setTemplateProcessor(TemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }
}
