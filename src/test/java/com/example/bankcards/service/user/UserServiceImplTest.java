package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.*;
import com.example.bankcards.entity.Role;
import static com.example.bankcards.dto.user.Role.ROLE_USER;
import static com.example.bankcards.dto.user.Role.ROLE_ADMIN;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.RoleNotFoundException;
import com.example.bankcards.exception.UserAlreadyExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private RoleRepository roleRepo;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("encodedPass");
        user.setRole(role);
    }

    @Test
    void createUser_ShouldSaveAndReturnDto() {
        CreateUserRequest req = new CreateUserRequest("newUser", "pass", ROLE_USER);

        when(userRepo.findByUsernameWithRole("newUser")).thenReturn(Optional.empty());
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(encoder.encode("pass")).thenReturn("encodedPass");
        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(99L);
            return u;
        });

        UserDto result = userService.createUser(req);

        assertThat(result.username()).isEqualTo("newUser");
        assertThat(result.role()).isEqualTo("ROLE_USER");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrow_WhenUserExists() {
        when(userRepo.findByUsernameWithRole("john")).thenReturn(Optional.of(user));

        CreateUserRequest req = new CreateUserRequest("john", "pass", ROLE_USER);

        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void getUserById_ShouldReturnDto() {
        when(userRepo.findByIdWithRole(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertThat(result.username()).isEqualTo("john");
    }

    @Test
    void getUserById_ShouldThrow_WhenNotFound() {
        when(userRepo.findByIdWithRole(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getAllUsers_ShouldReturnPagedResponse() {
        Pageable pageable = Pageable.ofSize(5);
        var ids = List.of(1L);
        Page<Long> idPage = new PageImpl<>(ids, pageable, 1);

        when(userRepo.findAllIds(pageable)).thenReturn(idPage);
        when(userRepo.findAllWithRolesByIdIn(ids)).thenReturn(List.of(user));

        UserPagedResponse response = userService.getAllUsers(pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void updateUserRole_ShouldUpdateRole() {
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ROLE_ADMIN");

        when(roleRepo.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepo.findByIdWithRole(1L)).thenReturn(Optional.of(user));

        UpdateUserRoleRequest req = new UpdateUserRoleRequest(ROLE_ADMIN);
        UserDto result = userService.updateUserRole(1L, req);

        assertThat(result.role()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void updateUserRole_ShouldThrow_WhenRoleNotFound() {
        when(roleRepo.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        UpdateUserRoleRequest req = new UpdateUserRoleRequest(ROLE_ADMIN);

        assertThatThrownBy(() -> userService.updateUserRole(1L, req))
                .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void updateUsername_ShouldChangeUsername() {
        UpdateUsernameRequest req = new UpdateUsernameRequest("newName");

        when(userRepo.findByUsernameWithRole("newName")).thenReturn(Optional.empty());
        when(userRepo.findByIdWithRole(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.updateUsername(1L, req);

        assertThat(result.username()).isEqualTo("newName");
    }

    @Test
    void updateUsername_ShouldThrow_WhenNewUsernameExists() {
        when(userRepo.findByUsernameWithRole("exists")).thenReturn(Optional.of(user));

        UpdateUsernameRequest req = new UpdateUsernameRequest("exists");

        assertThatThrownBy(() -> userService.updateUsername(1L, req))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void getByUsername_ShouldReturnUser() {
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("john");

        assertThat(result).isSameAs(user);
    }

    @Test
    void getByUsername_ShouldThrow_WhenNotFound() {
        when(userRepo.findByUsername("john")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByUsername("john"))
                .isInstanceOf(UserNotFoundException.class);
    }
}
