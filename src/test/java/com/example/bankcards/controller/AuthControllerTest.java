package com.example.bankcards.controller;

import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.dto.user.AuthRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

    @Test
    void login_success() throws Exception {
        // given
        AuthRequest request = new AuthRequest("user", "password");
        UserDetails userDetailsMock = Mockito.mock(UserDetails.class);
        Authentication authenticationMock = Mockito.mock(Authentication.class);

        when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
        when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
        when(jwtProvider.generateToken(userDetailsMock)).thenReturn("mockToken");

        // when + then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"mockToken\"}"));
    }

    @Test
    void login_badCredentials() throws Exception {
        AuthRequest request = new AuthRequest("user", "wrongpassword");

        Mockito.when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void login_ShouldReturnBadRequest_WhenValidationFails() throws Exception {

        String invalidPayload = """
        {
            "username": "",
            "password": ""
        }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
