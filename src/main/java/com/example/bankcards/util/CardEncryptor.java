package com.example.bankcards.util;

public interface CardEncryptor {

    String encrypt(String cardNumber);

    String decrypt(String encryptedCardNumber);

    String maskCardNumber(String cardNumber);
}
