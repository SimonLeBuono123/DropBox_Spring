package com.example.dropboxSpring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
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
    /**
     * Generates a random secret on every start/restart of program
     * Meaning that the token even if not expired will not be valid
     * on the next start of program as the secret will be changed.
     * This is to make it  a bit harder to decode a token.
     */
    private SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private String secret = Encoders.BASE64.encode(key.getEncoded());
    // 15 minutes until token expires
    private int jwtExpiration = (60 * 15);

    /**
     * Method for generating a token with given email and roles
     *
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
     * Method for extracting the email of token
     *
     * @param token
     * @return
     */
    public String extractEmail(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            validateToken(token);
        }
        return null;
    }

    /**
     * Method for extracting all claims and acquiring the body of the token (email).
     * @param token
     * @return
     */
    public Claims extractAllClaims(String token) {
        SecretKey secrets = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        return Jwts
                .parserBuilder()
                .setSigningKey(secrets)
                .build()
                .parseClaimsJws(token).getBody();
    }

    /**
     * Method for extracting the claim from the token.
     * @param token
     * @param claimsResolver
     * @return
     * @param <T>
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Method for checking validation of tokens with several
     * catches to check for any type of exception that may come
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            SecretKey secrets = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            Jwts.parserBuilder()
                    .setSigningKey(secrets)
                    .build()
                    .parseClaimsJws(token);
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
