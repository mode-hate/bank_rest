package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.*;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDto createUser(CreateUserRequest createRequest);

    UserDto getUserById(Long id);

    UserPagedResponse getAllUsers(Pageable pageable);

    UserDto updateUserRole(Long id, UpdateUserRoleRequest updateRequest);

    UserDto updateUsername(Long id, UpdateUsernameRequest updateUsernameRequest);

    User getByUsername(String username);
}
