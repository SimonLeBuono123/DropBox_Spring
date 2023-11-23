package com.example.dropboxSpring.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

@Slf4j
public class JwtUtility {
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    //this generates a random secret
    private final String secret = Encoders.BASE64.encode(key.getEncoded());
    // 15 minutes until token expires
    private int jwtExpiration = (60 * 15);
    public String generateToken(String username , Collection<? extends GrantedAuthority> roles) {
        SecretKey secrets = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder().setSubject(username).claim("role",roles).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.SECONDS)))
                .signWith(secrets, SignatureAlgorithm.HS256).compact();
    }
    //this extracts the username from the token so one can find the user via token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Claims extractAllClaims(String token) {
        SecretKey secrets = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.parserBuilder().setSigningKey(secrets).build().parseClaimsJws(token).getBody();}

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

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
