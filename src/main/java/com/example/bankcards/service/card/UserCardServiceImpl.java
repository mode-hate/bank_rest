package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.BalanceDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.NotCardHolderException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecifications;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCardServiceImpl implements CardUserService {

    private final CardRepository cardRepo;

    private final UserService userService;

    private final CardMapper cardMapper;


    @Override
    @Transactional(readOnly = true)
    public CardPagedResponse getUserCards(UserDetails principal, CardSearchRequest searchRequest) {

        var owner = userService.getByUsername(principal.getUsername());

        var pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());

        Specification<Card> spec = CardSpecifications.hasOwner(owner)
                .and(CardSpecifications.hasStatus(searchRequest.getStatus()))
                .and(CardSpecifications.fetchOwner());

        Page<Card> page = cardRepo.findAll(spec, pageable);

        return cardMapper.mapToCardPagedResponse(page);
    }

    @Override
    @Transactional
    public void requestBlockCard(Long cardId, UserDetails principal) {

        var card = getCardIfOwnedByUser(cardId, principal.getUsername());

        var currentStatus = card.getStatus();

        if (currentStatus != CardStatus.BLOCKED && currentStatus != CardStatus.BLOCK_REQUESTED){
            card.setStatus(CardStatus.BLOCK_REQUESTED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceDto showBalance(Long cardId, UserDetails principal) {

        var card = getCardIfOwnedByUser(cardId, principal.getUsername());

        return cardMapper.mapToBalance(card);
    }


    @Override
    public Card getCardById(Long cardId){
        return cardRepo.getByIdWithOwner(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }

    private Card getCardIfOwnedByUser(Long cardId, String username) {
        var owner = userService.getByUsername(username);
        var card = getCardById(cardId);

        if (!card.getOwner().equals(owner)) {
            throw new NotCardHolderException(owner.getUsername(), cardId);
        }

        return card;
    }

}
