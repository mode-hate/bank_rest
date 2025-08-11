package com.example.bankcards.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Component
@Slf4j
public class CardEncryptorAES implements CardEncryptor{

    private final SecretKeySpec secretKey;

    public CardEncryptorAES(@Value("${app.card.encryption-key}") String key) {
        byte[] keyBytes = key.getBytes();
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }


    @Override
    public String encrypt(String cardNumber) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(cardNumber.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            log.error("Error encrypting card number: {}", cardNumber);
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public String decrypt(String encryptedCardNumber) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decoded = Base64.getDecoder().decode(encryptedCardNumber);

            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Error decrypting card number: {}", encryptedCardNumber);
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
