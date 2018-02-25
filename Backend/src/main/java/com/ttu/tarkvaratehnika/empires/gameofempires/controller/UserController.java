package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountService;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:9000")
public class UserController {

    private AccountService accountService;
    private TemplateService templateService;

    @Autowired
    public UserController(AccountService accountService, TemplateService templateService) {
        this.accountService = accountService;
        this.templateService = templateService;
    }

    @RequestMapping(path = "/register")
    public @ResponseBody String signUp(@RequestParam String username, @RequestParam String pass) {
        boolean result = accountService.register(username, pass);
        return result ? "Registered" : "Failed to register";
    }

    @RequestMapping(path = "/login")
    public @ResponseBody String signIn(@RequestParam String username, @RequestParam String pass) {
        boolean result = accountService.logIn(username, pass);
        return result ? username : "";
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
}
