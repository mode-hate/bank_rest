package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.AuthSuccessResponse;
import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.security.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for users to log in")
public class AuthController {
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authManager;



    @Operation(summary = "users log in",
            description = "accepts username & password, returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful log in",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthSuccessResponse.class))),

            @ApiResponse(responseCode = "401", description = "invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthSuccessResponse> login(@Valid @RequestBody AuthRequest request) {

        var authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(new AuthSuccessResponse(token));
    }
}
