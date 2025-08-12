package com.example.bankcards.dto.card;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CardDto", description = "Data about a bank card")
public record CardDto(

        @Schema(description = "Unique identifier of the card", example = "123")
        Long id,

        @Schema(description = "Masked card number", example = "**** **** **** 1234")
        String maskedNumber,

        @Schema(description = "Name of the card owner", example = "john_doe")
        String ownerName,

        @Schema(description = "Card expiration date", example = "2025-12-31", type = "string", format = "date")
        LocalDate expiryDate,

        @Schema(description = "Current status of the card", example = "ACTIVE")
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
