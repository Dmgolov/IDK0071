package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.repository.UserRepository;
import com.ttu.tarkvaratehnika.empires.gameofempires.securityconstants.Constants;
import com.ttu.tarkvaratehnika.empires.gameofempires.user.User;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountService {

    private Map<String, LocalDateTime> loggedInUsers = new HashMap<>();
    private Map<String, String> userTokens = new HashMap<>();
    private UserRepository userRepository;

    @Autowired
    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String name, String email, String password)
            throws DataIntegrityViolationException, NoSuchAlgorithmException {
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
            LocalDateTime dateTime = LocalDateTime.now();
            Optional<String> token = Optional.ofNullable(generateToken(email, dateTime));
            while (!token.isPresent() || loggedInUsers.containsKey(token.get())) {
                token = Optional.ofNullable(generateToken(email, dateTime));
            }
            Optional<User> user = userRepository.getUserByEmail(email);
            if (user.isPresent()) {
                String stringToken = "Bearer " + token.get();
                loggedInUsers.put(stringToken, dateTime);
                userTokens.put(stringToken, user.get().getName());
                return token;
            }
        }
        return Optional.empty();
    }

    public void logOut(String token) {
        loggedInUsers.remove(token);
        userTokens.remove(token);
    }

    private boolean isRegistered(String email, String password) throws UnsupportedEncodingException {
        Optional<User> user = userRepository.getUserByEmail(email);
        if (user.isPresent()) {
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

    // TODO: implement later when signing in will be ready in frontend
    public boolean isLoggedIn(String token) {
//        return loggedInUsers.containsKey(token) && isSessionActive(token, LocalDateTime.now());
        return true;
    }

    private String generateToken(String email, LocalDateTime dateTime) throws UnsupportedEncodingException {
        Random random = new SecureRandom();
        byte[] bytes = (email + dateTime.toString()).getBytes("UTF-8");
        random.nextBytes(bytes);
        return Base64.encodeBase64String(bytes);
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

    private boolean isSessionActive(String token, LocalDateTime dateTime) {
        if (loggedInUsers.containsKey(token)) {
            if (Duration.between(dateTime, loggedInUsers.get(token)).toHours() < 2) {
                loggedInUsers.put(token, LocalDateTime.now());
                return true;
            } else {
                loggedInUsers.remove(token);
                userTokens.remove(token);
            }
        }
        return false;
    }

    public Optional<String> getUsernameForToken(String token) {
        return Optional.ofNullable(userTokens.get(token));
    }
}
