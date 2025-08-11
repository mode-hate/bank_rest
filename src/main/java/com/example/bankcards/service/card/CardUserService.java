package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.BalanceDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.entity.Card;
import org.springframework.security.core.userdetails.UserDetails;

public interface CardUserService {

    CardPagedResponse getUserCards(UserDetails principal, CardSearchRequest searchRequest);

    void requestBlockCard(Long cardId, UserDetails principal);

    BalanceDto showBalance(Long cardId, UserDetails principal);

    Card getCardById(Long cardId);
}
