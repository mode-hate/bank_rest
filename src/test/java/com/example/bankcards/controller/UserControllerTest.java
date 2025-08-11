package com.example.bankcards.controller;

import com.example.bankcards.BaseControllerTest;
import com.example.bankcards.dto.user.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {


    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_AsAdmin_ShouldReturnOk() throws Exception {
        var createRequest = new CreateUserRequest("testuser", "123456", Role.ROLE_USER);
        var responseDto = new UserDto(1L, "testuser", "ROLE_USER");

        Mockito.when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_AsAdmin_ShouldReturnUser() throws Exception {
        var responseDto = new UserDto(1L, "testuser", "ROLE_USER");

        Mockito.when(userService.getUserById(1L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_AsAdmin_ShouldReturnUpdatedUser() throws Exception {
        var updateRequest = new UpdateUserRoleRequest(Role.ROLE_ADMIN);
        var responseDto = new UserDto(1L, "testuser", "ROLE_ADMIN");

        Mockito.when(userService.updateUserRole(eq(1L), any(UpdateUserRoleRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUsername_AsAdmin_ShouldReturnUpdatedUser() throws Exception {
        var updateUsernameRequest = new UpdateUsernameRequest("newusername");
        var responseDto = new UserDto(1L, "newusername", "ROLE_USER");

        Mockito.when(userService.updateUsername(eq(1L), any(UpdateUsernameRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUsernameRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newusername"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithEmptyUsername_ShouldReturnBadRequest() throws Exception {
        var createRequest = new CreateUserRequest("", "12345", Role.ROLE_USER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_WithShortPassword_ShouldReturnBadRequest() throws Exception {
        var createRequest = new CreateUserRequest("validuser", "123", Role.ROLE_USER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUsername_WithEmptyUsername_ShouldReturnBadRequest() throws Exception {
        var updateUsernameRequest = new UpdateUsernameRequest("");

        mockMvc.perform(put("/api/users/1/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUsernameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_AsUser_ShouldReturnForbidden() throws Exception {
        var createRequest = new CreateUserRequest("testuser", "123456", Role.ROLE_USER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }

}