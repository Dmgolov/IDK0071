package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountProcessor;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//TODO: implement
//Handles incoming requests and calls required classes/methods
@RestController
public class RequestController {

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
        return result ? "Logged in" : "Failed to log in";
    }

    //TODO: later change pass to some kind of token
    @RequestMapping(path = "/lobby/new")
    public @ResponseBody String createNewLobby(@RequestParam String username, @RequestParam String pass,
                                 @RequestParam String lobbyName, @RequestParam String lobbyPass) {
        if (accountProcessor.isLoggedIn(username, pass)) {
            boolean result =  sessionController.createLobby(username, lobbyName, lobbyPass);
            return result ? "Created lobby" : "Failed to create a lobby";
        } else {
            return "Log in first";
        }
    }

    //TODO: check how to send back lists (as json?)
    @RequestMapping(path = "/lobby/all")
    public @ResponseBody List<String> getActiveLobbies(@RequestParam String filter) {
        return sessionController.getSessionNames(filter);
    }

    @RequestMapping(path = "/lobby/connect")
    public @ResponseBody String connectToLobby(@RequestParam String username, @RequestParam String pass,
                                               @RequestParam String lobbyName, @RequestParam String lobbyPass) {
        if (accountProcessor.isLoggedIn(username, pass)) {
            boolean result = sessionController.connectToLobby(username, lobbyName, lobbyPass);
            return result ? "Connected" : "Failed to connect";
        } else {
            return "Log in first";
        }
    }

    @RequestMapping(path = "/lobby/ready")
    public @ResponseBody String readyCheck(@RequestParam String username, @RequestParam long sessionId) {
        boolean result = sessionController.readyCheck(username, sessionId);
        return result ? "Ready" : "Failed to ready";
    }

    //TODO: change depending on how templates will be implemented
    @RequestMapping(path = "/template/new")
    public @ResponseBody String uploadTemplate(@RequestParam String username, @RequestParam String pass,
                                               @RequestParam String template) {
        if (accountProcessor.isLoggedIn(username, pass)) {
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
