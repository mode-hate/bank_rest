package com.example.bankcards.controller;

import com.example.bankcards.dto.card.BalanceDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.service.card.CardUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cards")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Fetching cards data", description = "API for users to see their cards data")
public class CardUserController {

    private final CardUserService cardService;


    @Operation(summary = "gets paginated cards list of the current user")
    @GetMapping
    public CardPagedResponse getUserCards(
            @Valid @ParameterObject CardSearchRequest searchRequest,
            @AuthenticationPrincipal UserDetails principal
    ){

        return cardService.getUserCards(principal, searchRequest);
    }


    @Operation(summary = "requests to show a card balance")
    @GetMapping("/{id}")
    public BalanceDto showCardBalance(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails principal
    ){

        return cardService.showBalance(id, principal);
    }


    @Operation(summary = "requests to block a card")
    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void requestBlockCard(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails principal
    ){
        cardService.requestBlockCard(id, principal);
    }
}
