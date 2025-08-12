package com.example.bankcards.util;

import com.example.bankcards.dto.card.BalanceDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.transfer.CardInfoDto;
import com.example.bankcards.dto.transfer.TransferDto;
import com.example.bankcards.dto.transfer.TransferPagedResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final CardEncryptor cardEncryptor;

    public CardDto mapToCardDto(Card card) {
        var cardNumber = cardEncryptor.decrypt(card.getEncryptedCardNumber());
        var maskedNumber  = cardEncryptor.maskCardNumber(cardNumber);
        return new CardDto(card, maskedNumber);
    }

    public CardPagedResponse mapToCardPagedResponse(Page<Card> page) {
        var cards = page.getContent()
                .stream()
                .map(this::mapToCardDto)
                .toList();

        return new CardPagedResponse(
                cards,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    public BalanceDto mapToBalance(Card card){
        var cardNumber = cardEncryptor.decrypt(card.getEncryptedCardNumber());
        return new BalanceDto(card.getBalance(), cardEncryptor.maskCardNumber(cardNumber));
    }


    public TransferDto mapToTransferDto(Transfer transfer){

        var fromCardInfo = mapToCardInfoDto(transfer.getFromCard());
        var toCardInfo = mapToCardInfoDto(transfer.getToCard());

        return new TransferDto(
                transfer.getId(),
                fromCardInfo,
                toCardInfo,
                transfer.getAmount(),
                transfer.getDescription(),
                transfer.getCreatedAt()
        );
    }

    private CardInfoDto mapToCardInfoDto(Card card){
        CardInfoDto cardInfo = null;

        if (card != null) {
            String cardNumber = cardEncryptor.decrypt(card.getEncryptedCardNumber());

            cardInfo = new CardInfoDto(
                    card.getId(),
                    cardEncryptor.maskCardNumber(cardNumber)
            );
        }
        return cardInfo;
    }


    public TransferPagedResponse mapToTransferPagedResponse(Page<Long> page, List<Transfer> transfers){
        var transfersDto = transfers.stream()
                .map(this::mapToTransferDto)
                .toList();

        return new TransferPagedResponse(
                transfersDto,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}
