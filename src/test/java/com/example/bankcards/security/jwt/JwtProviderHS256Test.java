package com.example.bankcards.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.nio.charset.StandardCharsets;
import java.util.List;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;


class JwtProviderHS256Test {
    private JwtProviderHS256 jwtProvider;

    private final String secret = "supersecretkeysupersecretkeysupersecretkey";
    private final long expirationMs = 3600000;

    private UserDetails user;


    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProviderHS256(secret, expirationMs);

        this.user = new User(
                "test_user",
                "test_password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtProvider.generateToken(user);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        String token = jwtProvider.generateToken(user);
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_ShouldReturnFalseForInvalidToken() {
        String badToken = "invalid.token.value";
        assertThat(jwtProvider.validateToken(badToken)).isFalse();
    }

    @Test
    void getUserDetails_ShouldReturnCorrectUserDetails() {

        String token = jwtProvider.generateToken(user);

        UserDetails parsedUser = jwtProvider.getUserDetails(token);

        assertThat(parsedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(parsedUser.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void validateToken_ShouldReturnFalseForExpiredToken() {

        String expiredToken = Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // issued yesterday
                .expiration(new Date(System.currentTimeMillis() - 1000)) // expired second ago
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();

        boolean isValid = jwtProvider.validateToken(expiredToken);

        assertThat(isValid).isFalse();
    }
}