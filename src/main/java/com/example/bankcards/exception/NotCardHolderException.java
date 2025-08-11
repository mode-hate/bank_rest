package com.example.bankcards.exception;

public class NotCardHolderException extends RuntimeException {
    public NotCardHolderException(String username, Long cardId) {
        super("%s is not a cardholder of the card with id: %d".formatted(username, cardId));
    }
}
