package com.ttu.tarkvaratehnika.empires.gameofempires.processor;

import com.ttu.tarkvaratehnika.empires.gameofempires.repository.UserRepository;
import com.ttu.tarkvaratehnika.empires.gameofempires.securityconstants.Constants;
import com.ttu.tarkvaratehnika.empires.gameofempires.user.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

//TODO: implement methods, add proper password protection, add more methods, if required
//Handles user actions with their accounts
@Service
public class AccountService {

    private Map<String, LocalDateTime> loggedInUsers = new HashMap<>();
    private UserRepository userRepository;

    public String register(String name, String password) throws DataIntegrityViolationException, GeneralSecurityException {
        User user = new User();
        byte[] salt = getSalt();
        user.setName(name);
        user.setPassword(cipherPassword(password, salt));
        user.setSalt(new String(salt));
        userRepository.save(user);
        return logIn(name, password);
    }

    public String logIn(String name, String password) {
        if (isRegistered(name, password)) {
            LocalDateTime dateTime = LocalDateTime.now();
            String token = null;
            try {
                while (token == null || loggedInUsers.containsKey(token)) {
                    token = generateToken(name, dateTime);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            loggedInUsers.put(token, dateTime);
            return token;
        }
        return null;
    }

    private boolean isRegistered(String name, String password) {
        Optional<User> user = userRepository.getUserByName(name);
        if (user.isPresent()) {
            byte[] salt = user.get().getSalt().getBytes();
            String hashedPass;
            try {
                hashedPass = cipherPassword(password, salt);
            } catch (GeneralSecurityException e) {
                System.out.println(e.getMessage());
                return false;
            }
            return user.get().getPassword().equals(hashedPass);
        }
        return false;
    }

    // TODO: implement later when signing in will be ready in frontend
    public boolean isLoggedIn(String token) {
        return true;
    }

    private String generateToken(String name, LocalDateTime dateTime) throws UnsupportedEncodingException {
        Random random = new SecureRandom();
        byte[] bytes = (name + dateTime.toString()).getBytes("UTF-8");
        random.nextBytes(bytes);
        return new String(bytes);
    }

    private String cipherPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null) {
            return null;
        }
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, Constants.NUM_OF_ITERATIONS,
                Constants.PASSWORD_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return new String(keyFactory.generateSecret(keySpec).getEncoded());
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[Constants.SALT_BYTE_ARRAY_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
}
