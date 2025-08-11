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
        var fromNumber = cardEncryptor.decrypt(transfer.getFromCard().getEncryptedCardNumber());
        var toNumber = cardEncryptor.decrypt(transfer.getToCard().getEncryptedCardNumber());

        return new TransferDto(
                transfer.getId(),
                new CardInfoDto(transfer.getFromCard().getId(), cardEncryptor.maskCardNumber(fromNumber)),
                new CardInfoDto(transfer.getToCard().getId(), cardEncryptor.maskCardNumber(toNumber)),
                transfer.getAmount(),
                transfer.getDescription(),
                transfer.getCreatedAt()
        );
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
