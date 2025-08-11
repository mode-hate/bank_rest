package com.example.bankcards.dto;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.List;


@RequiredArgsConstructor
@Getter
@Setter
@Schema(description = "Standard paginated response")
public class PagedResponse<T> {

    @ArraySchema(schema = @Schema(description = "List of page elements"))
    private final List<T> data;

    @Schema(description = "Total elements", example = "42")
    private final long totalElements;

    @Schema(description = "Total pages", example = "5")
    private final int totalPages;

    @Schema(description = "Zero indexed page number", example = "0")
    private final int pageNumber;

    @Schema(description = "Page size", example = "10")
    private final int pageSize;
}
