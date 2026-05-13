package solis3d.projectvaultbackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import solis3d.projectvaultbackend.entities.AppUser;
import solis3d.projectvaultbackend.exceptions.UnauthorizedException;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenTools {

    private final String secret;
    private final long expiration;

    public TokenTools(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(AppUser appUser) {
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .subject(String.valueOf(appUser.getId()))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public void verifyToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parse(token);
        } catch (Exception ex){
            throw new UnauthorizedException("Problemi con il token! Ripetere il login!");
        }
    }

    public UUID extractIdFromToken(String token) {
        return UUID.fromString(
                Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }
}
