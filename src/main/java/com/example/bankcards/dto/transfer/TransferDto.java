package com.example.bankcards.dto.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferDto(
        Long id,
        CardInfoDto fromCard,
        CardInfoDto toCard,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {}
