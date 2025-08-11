package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.BalanceDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.NotCardHolderException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCardServiceImplTest {

    @Mock
    private CardRepository cardRepo;
    @Mock
    private UserService userService;
    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private UserCardServiceImpl userCardService;

    private User owner;
    private Card card;
    private UserDetails principal;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("john");

        card = new Card();
        card.setId(100L);
        card.setOwner(owner);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryDate(LocalDate.now().plusYears(3L));

        principal = mock(UserDetails.class);
        lenient().when(principal.getUsername()).thenReturn("john");
    }

    @Test
    void getUserCards_ShouldReturnPagedResponse() {
        CardSearchRequest searchRequest = new CardSearchRequest();
        searchRequest.setPage(0);
        searchRequest.setSize(5);
        searchRequest.setStatus(CardStatus.ACTIVE);

        Page<Card> page = new PageImpl<>(List.of(card));
        CardPagedResponse response = new CardPagedResponse(List.of(), 1, 1, 0, 5);

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(cardMapper.mapToCardPagedResponse(page)).thenReturn(response);

        CardPagedResponse result = userCardService.getUserCards(principal, searchRequest);

        assertThat(result).isSameAs(response);
    }

    @Test
    void requestBlockCard_ShouldSetStatusToBlockRequested_WhenActive() {
        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardRepo.getByIdWithOwner(100L)).thenReturn(Optional.of(card));

        userCardService.requestBlockCard(100L, principal);

        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCK_REQUESTED);
    }

    @Test
    void requestBlockCard_ShouldNotChangeStatus_WhenAlreadyBlocked() {
        card.setStatus(CardStatus.BLOCKED);

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardRepo.getByIdWithOwner(100L)).thenReturn(Optional.of(card));

        userCardService.requestBlockCard(100L, principal);

        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void requestBlockCard_ShouldThrow_WhenNotOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("bob");

        card.setOwner(otherUser);

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardRepo.getByIdWithOwner(100L)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> userCardService.requestBlockCard(100L, principal))
                .isInstanceOf(NotCardHolderException.class);
    }

    @Test
    void showBalance_ShouldReturnBalanceDto() {
        BalanceDto balanceDto = new BalanceDto(card.getBalance(), card.getEncryptedCardNumber());

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardRepo.getByIdWithOwner(100L)).thenReturn(Optional.of(card));
        when(cardMapper.mapToBalance(card)).thenReturn(balanceDto);

        BalanceDto result = userCardService.showBalance(100L, principal);

        assertThat(result).isSameAs(balanceDto);
    }

    @Test
    void getCardById_ShouldReturnCard() {
        when(cardRepo.getByIdWithOwner(100L)).thenReturn(Optional.of(card));

        Card result = userCardService.getCardById(100L);

        assertThat(result).isSameAs(card);
    }

    @Test
    void getCardById_ShouldThrow_WhenNotFound() {
        when(cardRepo.getByIdWithOwner(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userCardService.getCardById(100L))
                .isInstanceOf(CardNotFoundException.class);
    }
}
