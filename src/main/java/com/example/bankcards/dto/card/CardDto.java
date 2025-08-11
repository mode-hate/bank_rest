package com.example.bankcards.dto.card;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import java.time.LocalDate;


public record CardDto(
        Long id,
        String maskedNumber,
        String ownerName,
        LocalDate expiryDate,
        CardStatus status
) {

    public CardDto(Card card, String maskedNumber){
        this(
                card.getId(),
                maskedNumber,
                card.getOwner().getUsername(),
                card.getExpiryDate(),
                card.getStatus()
        );
    }
}
