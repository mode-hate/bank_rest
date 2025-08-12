package com.example.bankcards.dto.card;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BalanceDto", description = "Represents card balance and masked card number")
public record BalanceDto(

        @Schema(description = "Current balance on the card", example = "1234.56")
        BigDecimal balance,

        @Schema(description = "Masked card number (e.g. **** **** **** 1234)", example = "**** **** **** 1234")
        String maskedCard
) {}