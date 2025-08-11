package com.example.bankcards.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


@Component
public class JwtProviderHS256 implements JwtProvider{

    private final long jwtExpirationMs;

    private final SecretKey secretKey;

    private static final String ROLE_CLAIMS = "roles";


    public JwtProviderHS256(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") long jwtExpirationMs){
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }


    @Override
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(ROLE_CLAIMS, userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }


    @SuppressWarnings("unchecked")
    @Override
    public UserDetails getUserDetails(String token){
        var claims = parseClaims(token).getPayload();

        String username = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = ((List<String>) claims.get(ROLE_CLAIMS))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        // org.springframework.security.core.userdetails.User
        return new User(username, "", authorities);
    }


    @Override
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}

