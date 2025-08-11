package com.example.bankcards.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id){
        super("User not found by id: " + id);
    }

    public UserNotFoundException(String username){
        super("User not found by username: " + username);
    }
}
