package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecifications;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class AdminCardServiceImpl implements CardAdminService{

    private final CardRepository cardRepo;

    private final UserService userService;

    private final CardEncryptor cardEncryptor;

    private final CardMapper cardMapper;

    @Value("${card.expiry.period}")
    private long expiryPeriod;


    @Override
    @Transactional(readOnly = true)
    public CardPagedResponse getAllCards(CardSearchRequest searchRequest) {

        var pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());

        Specification<Card> spec = Specification.<Card>unrestricted()
                .and(CardSpecifications.hasStatus(searchRequest.getStatus()))
                .and(CardSpecifications.fetchOwner());

        Page<Card> page = cardRepo.findAll(spec, pageable);

        return cardMapper.mapToCardPagedResponse(page);
    }

    @Override
    @Transactional
    public CardDto addCard(CreateCardRequest createRequest) {
        var owner = userService.getByUsername(createRequest.owner());

        var card = new Card();
        card.setOwner(owner);
        card.setEncryptedCardNumber(cardEncryptor.encrypt(createRequest.cardNumber()));
        card.setBalance(createRequest.balance());
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryDate(LocalDate.now().plusYears(expiryPeriod));

        cardRepo.save(card);

        return new CardDto(card, cardEncryptor.maskCardNumber(createRequest.cardNumber()));
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        var card = findCard(cardId);
        cardRepo.delete(card);
    }

    @Override
    @Transactional
    public void activateCard(Long cardId) {
        var card = findCard(cardId);

        if (card.getStatus() != CardStatus.EXPIRED){
            card.setStatus(CardStatus.ACTIVE);
        }
    }

    @Override
    @Transactional
    public void blockCard(Long cardId) {
        var card = findCard(cardId);

        if (card.getStatus() != CardStatus.BLOCKED){
            card.setStatus(CardStatus.BLOCKED);
        }

    }

    private Card findCard(Long cardId){
        return cardRepo.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }
}
