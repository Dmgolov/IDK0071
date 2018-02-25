package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//TODO: implement methods, add proper password protection, add more methods, if required
//Handles user actions with their accounts
@Service
public class AccountService {

    private Map<String, String> loggedInUsers = new HashMap<>();
    private UserRepository userRepository;

    public boolean register(String name, String password) {
        return false;
    }

    public boolean logIn(String name, String password) {
        return false;
    }

    public boolean isLoggedIn(String name) {
        return true;
    }
}
