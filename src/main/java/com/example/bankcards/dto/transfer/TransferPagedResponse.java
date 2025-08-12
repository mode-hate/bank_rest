package com.example.bankcards.dto.transfer;

import com.example.bankcards.dto.PagedResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of transfers")
public class TransferPagedResponse extends PagedResponse<TransferDto> {

    @Override
    @ArraySchema(schema = @Schema(implementation = TransferDto.class, description = "List of transfer objects"))
    public List<TransferDto> getData(){
        return super.getData();
    }

    public TransferPagedResponse(
            List<TransferDto> data,
            long totalElements,
            int totalPages,
            int pageNumber,
            int pageSize) {

        super(data, totalElements, totalPages, pageNumber, pageSize);
    }
}
