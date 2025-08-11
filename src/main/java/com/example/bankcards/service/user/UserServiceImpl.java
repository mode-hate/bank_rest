package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.*;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;

    private final PasswordEncoder encoder;


    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest createRequest) {

        checkIfUserExist(createRequest.username());

        var role = findRoleByName(createRequest.role().name());

        var user = new User();
        user.setUsername(createRequest.username());
        user.setPassword(encoder.encode(createRequest.password()));
        user.setRole(role);

        var savedUser = userRepo.save(user);

        return new UserDto(savedUser);
    }


    @Override
    public UserDto getUserById(Long id) {
        return findUserMapToDto(id, UserDto::new);
    }

    @Override
    @Transactional(readOnly = true)
    public UserPagedResponse getAllUsers(Pageable pageable) {

        var page = userRepo.findAllIds(pageable);

        var users = page.isEmpty()
                ? List.<UserDto>of()
                : userRepo.findAllWithRolesByIdIn(page.getContent())
                .stream()
                .map(UserDto::new)
                .toList();

        return new UserPagedResponse(
                users,
                page.getTotalElements(),
                page.getTotalPages(),
                pageable.getPageNumber(),
                page.getSize()
        );
    }


    @Override
    @Transactional
    public UserDto updateUserRole(Long id, UpdateUserRoleRequest updateRoleRequest) {

        var role = findRoleByName(updateRoleRequest.role().name());

        return findUserMapToDto(id, storedUser -> {
            storedUser.setRole(role);
            return new UserDto(storedUser);
        });
    }

    @Override
    @Transactional
    public UserDto updateUsername(Long id, UpdateUsernameRequest updateUsernameRequest) {

        checkIfUserExist(updateUsernameRequest.username());

        return findUserMapToDto(id, storedUser -> {
            storedUser.setUsername(updateUsernameRequest.username());
            return new UserDto(storedUser);
        });
    }


    @Override
    public User getByUsername(String username){
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }


    private UserDto findUserMapToDto(Long id, Function<User, UserDto> mapper){
        return userRepo.findByIdWithRole(id)
                .map(mapper)
                .orElseThrow(() -> new UserNotFoundException(id));
    }


    private Role findRoleByName(String roleName){
        return roleRepo.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));
    }

    private void checkIfUserExist(String username){
        var existedUser = userRepo.findByUsernameWithRole(username);
        if (existedUser.isPresent()) throw new UserAlreadyExistsException(username);
    }
}
