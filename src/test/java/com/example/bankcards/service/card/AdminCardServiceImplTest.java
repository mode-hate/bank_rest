package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardPagedResponse;
import com.example.bankcards.dto.card.CardSearchRequest;
import com.example.bankcards.dto.card.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceImplTest {

    @Mock
    private CardRepository cardRepo;
    @Mock
    private UserService userService;
    @Mock
    private CardEncryptor cardEncryptor;
    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private AdminCardServiceImpl adminCardService;

    private User owner;
    private Card card;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {

        owner = new User();
        owner.setId(1L);
        owner.setUsername("john");

        card = new Card();
        card.setId(100L);
        card.setOwner(owner);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryDate(LocalDate.now().plusYears(3L));

        var period = adminCardService.getClass()
                .getDeclaredField("expiryPeriod");

        period.setAccessible(true);
        period.setLong(adminCardService, 3L);
    }

    @Test
    void getAllCards_ShouldReturnPagedResponse() {
        CardSearchRequest request = new CardSearchRequest();
        request.setPage(0);
        request.setSize(5);
        request.setStatus(CardStatus.ACTIVE);

        Page<Card> page = new PageImpl<>(List.of(card));
        CardPagedResponse response = new CardPagedResponse(List.of(), 1, 1, 0, 5);

        when(cardRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(cardMapper.mapToCardPagedResponse(page)).thenReturn(response);

        CardPagedResponse result = adminCardService.getAllCards(request);

        assertThat(result).isSameAs(response);
    }

    @Test
    void addCard_ShouldSaveAndReturnDto() {
        CreateCardRequest req = new CreateCardRequest("1234567890123456", "john", BigDecimal.valueOf(500));

        when(userService.getByUsername("john")).thenReturn(owner);
        when(cardEncryptor.encrypt("1234567890123456")).thenReturn("encrypted");
        when(cardEncryptor.maskCardNumber("1234567890123456")).thenReturn("**** **** **** 3456");

        CardDto result = adminCardService.addCard(req);

        assertThat(result.maskedNumber()).isEqualTo("**** **** **** 3456");
        verify(cardRepo).save(any(Card.class));
    }

    @Test
    void deleteCard_ShouldDelete_WhenExists() {
        when(cardRepo.findById(100L)).thenReturn(Optional.of(card));

        adminCardService.deleteCard(100L);

        verify(cardRepo).delete(card);
    }

    @Test
    void deleteCard_ShouldThrow_WhenNotFound() {
        when(cardRepo.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCardService.deleteCard(100L))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void activateCard_ShouldSetStatusToActive_WhenNotExpired() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepo.findById(100L)).thenReturn(Optional.of(card));

        adminCardService.activateCard(100L);

        assertThat(card.getStatus()).isEqualTo(CardStatus.ACTIVE);
    }

    @Test
    void activateCard_ShouldNotChangeStatus_WhenExpired() {
        card.setStatus(CardStatus.EXPIRED);
        when(cardRepo.findById(100L)).thenReturn(Optional.of(card));

        adminCardService.activateCard(100L);

        assertThat(card.getStatus()).isEqualTo(CardStatus.EXPIRED);
    }

    @Test
    void blockCard_ShouldSetStatusToBlocked_WhenNotBlocked() {
        card.setStatus(CardStatus.ACTIVE);
        when(cardRepo.findById(100L)).thenReturn(Optional.of(card));

        adminCardService.blockCard(100L);

        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void blockCard_ShouldNotChangeStatus_WhenAlreadyBlocked() {
        card.setStatus(CardStatus.BLOCKED);
        when(cardRepo.findById(100L)).thenReturn(Optional.of(card));

        adminCardService.blockCard(100L);

        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }
}
