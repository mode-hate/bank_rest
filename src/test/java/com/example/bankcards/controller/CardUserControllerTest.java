package com.example.bankcards.controller;

import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.dto.card.BalanceDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CardUserController.class)
class CardUserControllerTest extends BaseControllerTest {

    CardPagedResponse response = new CardPagedResponse(
            List.of(new CardDto(1L,"**** ***** **** 1234", "Bob", LocalDate.now(), CardStatus.ACTIVE)),
            1L,
            1,
            0,
            10
    );

    @Test
    @WithMockUser()
    void getUserCards_ShouldReturnPagedCards() throws Exception {

        Mockito.when(cardUsrService.getUserCards(any(UserDetails.class), any(CardSearchRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser()
    void showCardBalance_ShouldReturnBalanceDto() throws Exception {
        BalanceDto balance = new BalanceDto(new BigDecimal("100.25"), "**** **** **** 1234");

        Mockito.when(cardUsrService.showBalance(eq(1L), any(UserDetails.class)))
                .thenReturn(balance);

        mockMvc.perform(get("/api/cards/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("100.25"));
    }

    @Test
    @WithMockUser()
    void requestBlockCard_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/api/cards/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(cardUsrService).requestBlockCard(eq(1L), any(UserDetails.class));
    }

    @Test
    @WithMockUser(roles = {"GUEST"})
    void getUserCards_ForbiddenForInvalidRole() throws Exception {
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser()
    void showCardBalance_InvalidId_ShouldReturnBadRequest() throws Exception {
        Mockito.when(cardUsrService.showBalance(eq(999L), any(UserDetails.class)))
                .thenThrow(new CardNotFoundException(999L));

        mockMvc.perform(get("/api/cards/{id}", 999L))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser()
    void getUserCards_WhenIncorrectStatusParam_ShouldReturnBadRequest() throws Exception {

        Mockito.when(cardUsrService.getUserCards(any(UserDetails.class), any(CardSearchRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "ACTIVe")
                )
                .andExpect(status().isBadRequest());
    }
}