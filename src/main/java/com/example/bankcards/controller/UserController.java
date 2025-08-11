package com.example.bankcards.controller;

import com.example.bankcards.dto.user.*;
import com.example.bankcards.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/users")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Managing users", description = "API for admins to manage users")
public class UserController {

    private final UserService userService;


    @Operation(summary = "creates new user", description = "Only accessible by ADMIN role")
    @PostMapping
    public UserDto createUser(@Valid @RequestBody CreateUserRequest createRequest){
        return userService.createUser(createRequest);
    }


    @Operation(summary = "retrieves user by id", description = "Only accessible by ADMIN role")
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }


    @Operation(summary = "retrieve all users (paginated)", description = "Only accessible by ADMIN role")
    @GetMapping
    public UserPagedResponse getAllUsers(@PageableDefault() Pageable pageable){
        return userService.getAllUsers(pageable);
    }


    @Operation(summary = "changes user role", description = "Only accessible by ADMIN role")
    @PutMapping("/{id}/role")
    public UserDto updateUserRole(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserRoleRequest updateRequest){

        return userService.updateUserRole(id, updateRequest);
    }


    @Operation(summary = "changes username", description = "Only accessible by ADMIN role")
    @PutMapping("/{id}/username")
    public UserDto updateUsername(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUsernameRequest updateUsernameRequest){

        return userService.updateUsername(id, updateUsernameRequest);
    }
}
