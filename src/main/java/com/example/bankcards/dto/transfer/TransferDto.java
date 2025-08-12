package com.example.bankcards.dto.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TransferDto", description = "Information about a card transfer")
public record TransferDto(

        @Schema(description = "Transfer unique identifier", example = "12")
        Long id,

        @Schema(description = "Source card information")
        CardInfoDto fromCard,

        @Schema(description = "Destination card information")
        CardInfoDto toCard,

        @Schema(description = "Amount transferred", example = "150.75")
        BigDecimal amount,

        @Schema(description = "Transfer description", example = "Payment for invoice #1234")
        String description,

        @Schema(description = "Date and time when transfer was created", example = "2025-08-12T14:30:00")
        LocalDateTime createdAt
) {}
