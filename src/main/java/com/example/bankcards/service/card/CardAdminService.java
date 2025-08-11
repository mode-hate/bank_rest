package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.dto.card.CreateCardRequest;

public interface CardAdminService {

    CardPagedResponse getAllCards(CardSearchRequest searchRequest);

    CardDto addCard(CreateCardRequest createRequest);

    void blockCard(Long cardId);

    void activateCard(Long cardId);

    void deleteCard(Long cardId);
}
