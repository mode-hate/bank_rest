package com.example.bankcards.dto.card;

import com.example.bankcards.dto.PagedResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of cards")
public class CardPagedResponse extends PagedResponse<CardDto> {

    public CardPagedResponse(List<CardDto> data, long totalElements, int totalPages, int pageNumber, int pageSize) {
        super(data, totalElements, totalPages, pageNumber, pageSize);
    }
}
