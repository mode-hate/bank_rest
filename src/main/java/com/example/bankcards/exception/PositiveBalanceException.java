package com.example.bankcards.exception;

public class PositiveBalanceException extends RuntimeException {
    public PositiveBalanceException(Long id) {
        super("Cannot delete a card with funds, card id: %d".formatted(id));
    }
}
