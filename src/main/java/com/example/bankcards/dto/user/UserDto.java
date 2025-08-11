package com.example.bankcards.dto.user;

import com.example.bankcards.entity.User;


public record UserDto(
        long id,
        String username,
        String role
) {

    public UserDto(User user){
        this(
                user.getId(),
                user.getUsername(),
                user.getRole().getName()
        );
    }
}