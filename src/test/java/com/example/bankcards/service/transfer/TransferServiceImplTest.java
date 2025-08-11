package com.example.bankcards.service.transfer;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.example.bankcards.dto.transfer.CardInfoDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {
    @Mock
    private TransferRepository transferRepo;
    @Mock
    private UserService userService;
    @Mock
    private CardUserService cardService;
    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private User owner;
    private Card fromCard;
    private Card toCard;
    private UserDetails principal;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("john");

        fromCard = new Card();
        fromCard.setId(100L);
        fromCard.setBalance(BigDecimal.valueOf(500));
        fromCard.setOwner(owner);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDate.now().plusYears(3L));

        toCard = new Card();
        toCard.setId(200L);
        toCard.setBalance(BigDecimal.valueOf(300));
        toCard.setOwner(owner);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpiryDate(LocalDate.now().plusYears(3L));

        principal = mock(UserDetails.class);
        when(principal.getUsername()).thenReturn("john");
    }

    @Test
    void performTransfer_ShouldUpdateBalances_AndReturnDto() {
        TransferRequest request = new TransferRequest(100L, 200L, BigDecimal.valueOf(100), "test");

        TransferDto transferDto = new TransferDto(
                1L,
                new CardInfoDto(12L, "*************1111"),
                new CardInfoDto(433L, "************1233"),
                request.amount(),
                request.description(),
                LocalDateTime.now()
        );

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardService.getCardById(100L)).thenReturn(fromCard);
        when(cardService.getCardById(200L)).thenReturn(toCard);
        when(cardMapper.mapToTransferDto(any(Transfer.class))).thenReturn(transferDto);

        TransferDto result = transferService.performTransfer(request, principal);

        assertThat(fromCard.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(400));
        assertThat(toCard.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(400));
        assertThat(result).isSameAs(transferDto);

        verify(transferRepo).save(any(Transfer.class));
    }

    @Test
    void performTransfer_ShouldThrow_WhenSameCard() {
        TransferRequest request = new TransferRequest(100L, 100L, BigDecimal.valueOf(50), "same");

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardService.getCardById(100L)).thenReturn(fromCard);

        assertThatThrownBy(() -> transferService.performTransfer(request, principal))
                .isInstanceOf(CardTransferException.class)
                .hasMessage("Cannot transfer to the same card");

        verifyNoInteractions(transferRepo);
    }

    @Test
    void performTransfer_ShouldThrow_WhenInsufficientBalance() {
        fromCard.setBalance(BigDecimal.valueOf(10));
        TransferRequest request = new TransferRequest(100L, 200L, BigDecimal.valueOf(50), "not enough");

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardService.getCardById(100L)).thenReturn(fromCard);
        when(cardService.getCardById(200L)).thenReturn(toCard);

        assertThatThrownBy(() -> transferService.performTransfer(request, principal))
                .isInstanceOf(CardTransferException.class)
                .hasMessage("Insufficient balance");

        verifyNoInteractions(transferRepo);
    }

    @Test
    void getCardTransfers_ShouldReturnPagedResponse() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Long> pageIds = new PageImpl<>(List.of(1L, 2L), pageable, 2);
        List<Transfer> transfers = List.of(new Transfer(), new Transfer());
        List<TransferDto> transferDto = List.of();
        TransferPagedResponse response = new TransferPagedResponse(transferDto, 2L,1,0, 10);

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardService.getCardById(100L)).thenReturn(fromCard);
        when(transferRepo.findTransferIdsByCardId(100L, pageable)).thenReturn(pageIds);
        when(transferRepo.findAllByIds(List.of(1L, 2L))).thenReturn(transfers);
        when(cardMapper.mapToTransferPagedResponse(pageIds, transfers)).thenReturn(response);

        TransferPagedResponse result = transferService.getCardTransfers(100L, principal, pageable);

        assertThat(result).isSameAs(response);
    }

    @Test
    void getCardTransfers_ShouldThrow_WhenNotOwner() {
        Card otherCard = new Card();
        otherCard.setId(999L);
        otherCard.setOwner(new User());

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardService.getCardById(999L)).thenReturn(otherCard);

        assertThatThrownBy(() -> transferService.getCardTransfers(999L, principal, Pageable.unpaged()))
                .isInstanceOf(NotCardHolderException.class);
    }

    @Test
    void performTransfer_ShouldThrow_WhenCardBlockedOrExpired() {

        fromCard.setStatus(CardStatus.BLOCKED);
        fromCard.setExpiryDate(LocalDate.now().plusMonths(1));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpiryDate(LocalDate.now().plusMonths(1));

        TransferRequest request = new TransferRequest(100L, 200L, BigDecimal.valueOf(50), "blocked");

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardService.getCardById(100L)).thenReturn(fromCard);
        when(cardService.getCardById(200L)).thenReturn(toCard);

        assertThatThrownBy(() -> transferService.performTransfer(request, principal))
                .isInstanceOf(CardTransferException.class)
                .hasMessage("Cannot use blocked or expired card");


        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> transferService.performTransfer(request, principal))
                .isInstanceOf(CardTransferException.class)
                .hasMessage("Cannot use blocked or expired card");
    }
}