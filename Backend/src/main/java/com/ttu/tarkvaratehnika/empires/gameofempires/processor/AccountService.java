package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.messagekeys.MessageKeys;
import com.ttu.tarkvaratehnika.empires.gameofempires.repository.UserRepository;
import com.ttu.tarkvaratehnika.empires.gameofempires.security.Constants;
import com.ttu.tarkvaratehnika.empires.gameofempires.security.TokenService;
import com.ttu.tarkvaratehnika.empires.gameofempires.user.User;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

@Service
public class AccountService {

    private UserRepository userRepository;
    private TokenService tokenService;

    @Autowired
    public AccountService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public void register(String name, String email, String password)
            throws DataIntegrityViolationException, NoSuchAlgorithmException, IllegalArgumentException {
        checkDataIntegrity(name, email, password);
        User user = new User();
        byte[] salt = getSalt().getBytes();
        user.setName(name);
        user.setEmail(email);
        String pass = encryptPassword(password, salt);
        user.setPassword(pass);
        user.setSalt(new String(salt));
        userRepository.save(user);
    }

    public Optional<String> logIn(String email, String password) throws UnsupportedEncodingException {
        if (isRegistered(email, password)) {
            Optional<User> user = userRepository.getUserByEmail(email);
            if (user.isPresent()) {
                return Optional.ofNullable(tokenService.generateToken(user.get().getName()));
            }
        }
        return Optional.empty();
    }

    public void logOut() {

    }

    private boolean isRegistered(String email, String password) throws UnsupportedEncodingException {
        Optional<User> user = userRepository.getUserByEmail(email);
        if (user.isPresent() && password != null) {
            byte[] salt = user.get().getSalt().getBytes("UTF-8");
            try {
                String hashedPass = encryptPassword(password, salt);
                return hashedPass.equals(user.get().getPassword());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String encryptPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (Byte b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16));
        }
        return sb.toString();
    }

    private String getSalt() {
        Random random = new SecureRandom();
        byte[] salt = new byte[Constants.SALT_BYTE_ARRAY_LENGTH];
        random.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    public Optional<String> getUsernameForToken(String token) {
        return tokenService.getUsernameFromToken(token);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByName(username);
    }

    private void checkDataIntegrity(String username, String email, String password) throws IllegalArgumentException {
        Optional<User> user = userRepository.getUserByEmail(email);
        if (user.isPresent()) {
            throw new IllegalArgumentException(MessageKeys.DUPLICATE_EMAIL);
        }
        user = userRepository.getUserByName(username);
        if (user.isPresent()) {
            throw new IllegalArgumentException(MessageKeys.DUPLICATE_NAME);
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.EMPTY_NAME);
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.EMPTY_EMAIL);
        }
        if (username.equals("null") || username.startsWith("Bot ")) {
            throw new IllegalArgumentException(MessageKeys.RESTRICTED_NAME);
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(MessageKeys.EMPTY_PASS);
        }
        if (password.length() < Constants.PASSWORD_MIN_LENGTH) {
            throw new IllegalArgumentException(MessageKeys.SHORT_PASS);
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException(MessageKeys.INVALID_EMAIL);
        }
    }
}
