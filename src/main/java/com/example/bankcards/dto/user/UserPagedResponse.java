package com.example.bankcards.dto.user;

import com.example.bankcards.dto.PagedResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of users")
public class UserPagedResponse extends PagedResponse<UserDto> {

    public UserPagedResponse(List<UserDto> data, long totalElements, int totalPages, int pageNumber, int pageSize) {
        super(data, totalElements, totalPages, pageNumber, pageSize);
    }
}