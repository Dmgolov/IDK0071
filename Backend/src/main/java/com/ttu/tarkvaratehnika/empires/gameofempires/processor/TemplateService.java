package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.repository.GameTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO: implement (?)
//Handles templates creation, loading, saving
@Service
public class TemplateService {

    private GameTemplateRepository repository;

    public boolean addNewTemplate(String username, String template) {
        return false;
    }

    public List<String> findTemplates(String filter) {
        return null;
    }
}
