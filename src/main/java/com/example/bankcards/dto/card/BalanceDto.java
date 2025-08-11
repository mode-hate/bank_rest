package com.example.bankcards.dto.card;

import java.math.BigDecimal;


public record BalanceDto(BigDecimal balance, String maskedCard) {}
