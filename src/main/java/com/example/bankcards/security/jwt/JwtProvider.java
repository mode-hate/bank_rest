package com.example.bankcards.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtProvider {

    String generateToken(UserDetails userDetails);

    boolean validateToken(String token);

    UserDetails getUserDetails(String token);
}
