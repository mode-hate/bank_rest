package com.example.bankcards.controller;

import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.dto.transfer.CardInfoDto;
import com.example.bankcards.dto.transfer.TransferDto;
import com.example.bankcards.dto.transfer.TransferPagedResponse;
import com.example.bankcards.dto.transfer.TransferRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransfersController.class)
class TransfersControllerTest extends BaseControllerTest {

    TransferDto transferDto = new TransferDto(
            1L,
            new CardInfoDto(1L, "*************1234"),
            new CardInfoDto(2L, "**********3456"),
            new BigDecimal("50.75"),
            "Test payment",
            LocalDateTime.now()
    );

    @Test
    @WithMockUser
    void performTransfer_ValidRequest_ShouldReturnTransferDto() throws Exception {
        var request = new TransferRequest(1L, 2L, new BigDecimal("50.75"), "Test payment");

        Mockito.when(transferService.performTransfer(any(TransferRequest.class), any(UserDetails.class)))
                .thenReturn(transferDto);

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value("50.75"))
                .andExpect(jsonPath("$.description").value("Test payment"));
    }

    @Test
    @WithMockUser
    void getCardTransfers_ValidRequest_ShouldReturnPagedResponse() throws Exception {
        Long cardId = 1L;
        var pagedResponse = new TransferPagedResponse(
                List.of(transferDto),
                1L,
                1,
                0,
                20
        );

        Mockito.when(transferService.getCardTransfers(eq(cardId), any(UserDetails.class), any(Pageable.class)))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/transfers/{cardId}", cardId)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = {"GUEST"})
    void performTransfer_WithoutProperRole_ShouldReturnForbidden() throws Exception {
        var request = new TransferRequest(1L, 2L, new BigDecimal("10.00"), "Test payment");

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void performTransfer_WithWrongAmount_ShouldReturnBadRequest() throws Exception {
        var request = new TransferRequest(1L, 2L, BigDecimal.ZERO, "Test payment");

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}