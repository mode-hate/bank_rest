package com.example.bankcards.controller;

import com.example.bankcards.dto.transfer.TransferDto;
import com.example.bankcards.dto.transfer.TransferPagedResponse;
import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.service.transfer.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Transfers between users cards", description = "API for users to make transfers between their cards")
public class TransfersController {

    private final TransferService transferService;


    @Operation(summary = "performs a transfer between 2 user cards")
    @PostMapping
    public TransferDto performTransfer(
            @Valid @RequestBody TransferRequest transferRequest,
            @AuthenticationPrincipal UserDetails principal
            ){
        return transferService.performTransfer(transferRequest, principal);
    }


    @Operation(summary = "finds paginated list of transfers for a card")
    @GetMapping("/{cardId}")
    public TransferPagedResponse getCardTransfers(
            @PathVariable("cardId") Long cardId,
            @AuthenticationPrincipal UserDetails principal,
            @PageableDefault(direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable
    ){

        return transferService.getCardTransfers(cardId, principal, pageable);
    }
}
