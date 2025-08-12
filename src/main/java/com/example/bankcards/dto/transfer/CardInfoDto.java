package com.example.bankcards.dto.transfer;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CardInfoDto", description = "Basic info about a card")
public record CardInfoDto(

        @Schema(description = "Card unique identifier", example = "1")
        Long id,

        @Schema(description = "Masked card number", example = "**** **** **** 5678")
        String maskedNumber
) {}
