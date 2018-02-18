package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.repository.UserRepository;

//TODO: implement methods, add proper password protection, add more methods, if required
//Handles user actions with their accounts
public class AccountProcessor {

    private UserRepository userRepository;

    public boolean register(String name, String password) {
        return false;
    }

    public boolean deleteUser(String name) {
        return false;
    }

    public String hashPassword(String password) {
        return null;
    }


}
