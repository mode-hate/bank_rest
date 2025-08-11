package com.example.bankcards.controller;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.service.card.CardAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Managing users cards", description = "API for admins to manage cards")
public class CardAdminController {

    private final CardAdminService cardService;


    @Operation(summary = "gets paginated cards list")
    @GetMapping
    public CardPagedResponse getAllCards(@Valid @ParameterObject CardSearchRequest searchRequest){
        return cardService.getAllCards(searchRequest);
    }


    @Operation(summary = "creates a new card")
    @PostMapping
    public CardDto createCard(@Valid @RequestBody CreateCardRequest createCardRequest){
        return cardService.addCard(createCardRequest);
    }


    @Operation(summary = "deletes a card")
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable("id") Long id){
        cardService.deleteCard(id);
    }


    @Operation(summary = "activates a card")
    @PutMapping("/{id}/activate")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void activateCard(@PathVariable("id") Long id){
        cardService.activateCard(id);
    }


    @Operation(summary = "blocks a card")
    @PutMapping("/{id}/block")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void blockCard(@PathVariable("id") Long id){
        cardService.blockCard(id);
    }


}
