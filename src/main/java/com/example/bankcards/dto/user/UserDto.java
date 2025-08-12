package com.example.bankcards.dto.user;

import com.example.bankcards.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserDto", description = "User data transfer object")
public record UserDto(

        @Schema(description = "User unique identifier", example = "1")
        long id,

        @Schema(description = "Username of the user", example = "john_doe")
        String username,

        @Schema(description = "Role assigned to the user", example = "ROLE_USER")
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