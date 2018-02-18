package com.ttu.tarkvaratehnika.empires.gameofempires.controller;

import com.ttu.tarkvaratehnika.empires.gameofempires.processor.AccountProcessor;
import com.ttu.tarkvaratehnika.empires.gameofempires.processor.TemplateProcessor;
import org.springframework.web.bind.annotation.RestController;

//TODO: implement
//Handles incoming requests and calls required classes/methods
@RestController
public class RequestController {

    private SessionController sessionController;
    private AccountProcessor accountProcessor;
    private TemplateProcessor templateProcessor;

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
