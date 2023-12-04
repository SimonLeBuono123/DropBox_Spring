package com.example.dropboxSpring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

/**
 * Class for handling tokens and creating tokens.
 */
@Slf4j
@Component
public class JwtUtility {
    @Value("${jwt.secret}")
    private String secret;
    // 15 minutes until token expires
    private int jwtExpiration = (60 * 15);

    /**
     * Method for generating a token with given email and roles
     * @param email
     * @param roles
     * @return
     */
    public String generateToken(String email, Collection<? extends GrantedAuthority> roles) {
        SecretKey secrets = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .setSubject(email).claim("role", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.SECONDS)))
                .signWith(secrets, SignatureAlgorithm.HS256).compact();
    }

    /**
     * Method for extracting email of token
     * @param token
     * @return
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Claims extractAllClaims(String token) {
        SecretKey secrets = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts
                .parserBuilder()
                .setSigningKey(secrets)
                .build()
                .parseClaimsJws(token).getBody();
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Method for checking validation of tokens with several
     * catches to check for any type of exception
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            SecretKey secrets = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Jwts.parserBuilder().setSigningKey(secrets).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
           log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return false;
    }
}
