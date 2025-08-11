package com.example.bankcards.service.transfer;

import com.example.bankcards.dto.transfer.TransferDto;
import com.example.bankcards.dto.transfer.TransferPagedResponse;
import com.example.bankcards.dto.transfer.TransferRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;


public interface TransferService {

    TransferDto performTransfer(TransferRequest transferRequest, UserDetails principal);

    TransferPagedResponse getCardTransfers(Long cardId, UserDetails principal, Pageable pageable);
}
