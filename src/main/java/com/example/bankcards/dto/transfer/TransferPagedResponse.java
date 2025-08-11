package com.example.bankcards.dto.transfer;

import com.example.bankcards.dto.PagedResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of transfers")
public class TransferPagedResponse extends PagedResponse<TransferDto> {

    public TransferPagedResponse(List<TransferDto> data, long totalElements, int totalPages, int pageNumber, int pageSize) {
        super(data, totalElements, totalPages, pageNumber, pageSize);
    }
}
