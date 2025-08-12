package com.example.bankcards;

import com.example.bankcards.security.jwt.JwtProvider;
import com.example.bankcards.service.card.CardAdminService;
import com.example.bankcards.service.card.CardUserService;
import com.example.bankcards.service.transfer.TransferService;
import com.example.bankcards.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(TestSecurityConfig.class)
public abstract class BaseControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    @MockitoBean
    public UserService userService;

    @MockitoBean
    public JwtProvider jwtProvider;

    @MockitoBean
    public AuthenticationManager authenticationManager;

    @MockitoBean
    public TransferService transferService;

    @MockitoBean
    public CardAdminService cardAdmService;

    @MockitoBean
    public CardUserService cardUsrService;

}