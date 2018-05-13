package com.ttu.tarkvaratehnika.empires.gameofempires.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    private SecretKey JWTS_KEY;
    private final int EXPIRATION_TIME = 180;
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    public TokenService() {
        generateSecretKeyToSignTokens();
    }

    private void generateSecretKeyToSignTokens() {
        this.JWTS_KEY = MacProvider.generateKey(SIGNATURE_ALGORITHM);
    }

    public String generateToken(String username) {
        Date now = new Date(System.currentTimeMillis());
        Date dateLimit = new Date(now.getTime() + EXPIRATION_TIME * 60 * 1000);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(dateLimit)
                .signWith(SIGNATURE_ALGORITHM, JWTS_KEY)
                .compact();
    }

    public boolean isActive(String token) {
        try {
            Optional<Date> expiration = Optional.of(Jwts.parser()
                    .setSigningKey(JWTS_KEY)
                    .parseClaimsJws(removeBearerPrefix(token))
                    .getBody()
                    .getExpiration());
            return expiration.isPresent() && !hasExpirationTimePassed(expiration.get());
        } catch (SignatureException | NullPointerException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Optional<String> getUsernameFromToken(String authToken) throws SignatureException {
        try {
            return Optional.of(Jwts.parser()
                    .setSigningKey(JWTS_KEY)
                    .parseClaimsJws(removeBearerPrefix(authToken))
                    .getBody()
                    .getSubject());
        } catch (SignatureException | NullPointerException e) {
            return Optional.empty();
        }
    }

    private String removeBearerPrefix(String token) {
        return token.substring(7);
    }

    private boolean hasExpirationTimePassed(Date date) {
        return date.before(new Date(System.currentTimeMillis()));
    }
}
