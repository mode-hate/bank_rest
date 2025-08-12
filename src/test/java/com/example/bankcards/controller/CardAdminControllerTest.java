package com.example.bankcards.controller;

import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.entity.enums.CardStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CardAdminController.class)
class CardAdminControllerTest extends BaseControllerTest {

    CardDto cardDto = new CardDto(
            14L,
            "1234567812345678",
            "user1",
            LocalDate.now().plusYears(3L),
            CardStatus.ACTIVE
    );

    String createCardRequest = """
                {
                    "cardNumber": "1234567812345678",
                    "owner": "user1",
                    "balance": 100.00
                }
                """;


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_ShouldReturnOk() throws Exception {
        CardPagedResponse response = new CardPagedResponse(List.of(), 0, 0, 0, 0);
        Mockito.when(cardAdmService.getAllCards(any(CardSearchRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getAllCards_WhenNotAdmin_ShouldReturnForbidden() throws Exception {
        CardPagedResponse response = new CardPagedResponse(List.of(), 0, 0, 0, 0);
        Mockito.when(cardAdmService.getAllCards(any(CardSearchRequest.class))).thenReturn(response);

        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_Valid_ShouldReturnOk() throws Exception {

        Mockito.when(cardAdmService.addCard(any(CreateCardRequest.class))).thenReturn(cardDto);

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCardRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerName").value("user1"))
                .andExpect(jsonPath("$.id").value(14L));
    }

    @Test
    @WithMockUser
    void createCard_WhenNotAdmin_ShouldReturnForbidden() throws Exception {

        Mockito.when(cardAdmService.addCard(any(CreateCardRequest.class))).thenReturn(cardDto);

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCardRequest))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_Invalid_ShouldReturnBadRequest() throws Exception {
        String invalidJson = """
                {
                    "cardNumber": "123",
                    "owner": "",
                    "balance": -50
                }
                """;

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/cards/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(cardAdmService).deleteCard(1L);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void activateCard_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/api/admin/cards/{id}/activate", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(cardAdmService).activateCard(1L);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/api/admin/cards/{id}/block", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(cardAdmService).blockCard(1L);
    }
}