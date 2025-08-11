package com.example.bankcards.service.transfer;

import com.example.bankcards.dto.transfer.TransferDto;
import com.example.bankcards.dto.transfer.TransferPagedResponse;
import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardTransferException;
import com.example.bankcards.exception.NotCardHolderException;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.card.CardUserService;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService{

    private final TransferRepository transferRepo;

    private final UserService userService;

    private final CardUserService cardService;

    private final CardMapper cardMapper;


    @Override
    @Transactional
    public TransferDto performTransfer(TransferRequest transferRequest, UserDetails principal) {
        var owner = userService.getByUsername(principal.getUsername());
        var fromCard = cardService.getCardById(transferRequest.fromCardId());
        var toCard = cardService.getCardById(transferRequest.toCardId());

        validateTransfer(owner, fromCard, toCard, transferRequest.amount());

        var transfer = createTransfer(owner, fromCard, toCard, transferRequest);

        updateBalances(fromCard, toCard, transferRequest.amount());

        transferRepo.save(transfer);

        return cardMapper.mapToTransferDto(transfer);
    }


    private void updateBalances(Card fromCard, Card toCard, BigDecimal amount) {
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
    }


    private void validateTransfer(User owner, Card fromCard, Card toCard, BigDecimal amount) {

        validateCardStatus(fromCard);
        validateCardStatus(toCard);

        validateOwnership(owner, fromCard);
        validateOwnership(owner, toCard);

        if (fromCard.getId().equals(toCard.getId())) {
            throw new CardTransferException("Cannot transfer to the same card");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new CardTransferException("Insufficient balance");
        }
    }

    private void validateOwnership(User owner, Card card){
        if (!card.getOwner().equals(owner)){
            throw new NotCardHolderException(owner.getUsername(), card.getId());
        }
    }


    private void validateCardStatus(Card card){
        if (card.getStatus() != CardStatus.ACTIVE || LocalDate.now().isAfter(card.getExpiryDate())){
            throw new CardTransferException("Cannot use blocked or expired card");
        }
    }

    private Transfer createTransfer(User owner, Card fromCard, Card toCard, TransferRequest request) {
        var transfer = new Transfer();
        transfer.setOwner(owner);
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);
        transfer.setAmount(request.amount());
        transfer.setDescription(request.description());
        transfer.setCreatedAt(LocalDateTime.now());
        return transfer;
    }



    @Override
    @Transactional(readOnly = true)
    public TransferPagedResponse getCardTransfers(Long cardId, UserDetails principal, Pageable pageable) {
        var owner = userService.getByUsername(principal.getUsername());
        var fromCard = cardService.getCardById(cardId);

        validateOwnership(owner, fromCard);

        var page = transferRepo.findTransferIdsByCardId(cardId, pageable);

        var transfers = page.isEmpty() ?
                List.<Transfer>of() : transferRepo.findAllByIds(page.getContent());

        return cardMapper.mapToTransferPagedResponse(page, transfers);
    }
}