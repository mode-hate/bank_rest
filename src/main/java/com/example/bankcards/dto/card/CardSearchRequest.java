package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "CardSearchRequest", description = "Request parameters for searching and paging cards")
public class CardSearchRequest {

    @PositiveOrZero(message = "Page index must be zero or positive")
    @Schema(description = "Zero-based page index", example = "0", defaultValue = "0")
    private int page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 50, message = "Page size must be at most 50")
    @Schema(description = "Number of items per page", example = "10", defaultValue = "10")
    private int size = 10;

    @Schema(description = "Filter cards by status", example = "ACTIVE")
    private CardStatus status;
}
