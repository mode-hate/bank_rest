package com.example.bankcards.security;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        Role role = new Role();
        role.setName("ROLE_USER");

        User userEntity = new User();
        userEntity.setUsername("test_user");
        userEntity.setPassword("encodedPass");
        userEntity.setRole(role);

        when(userRepository.findByUsernameWithRole("test_user")).thenReturn(Optional.of(userEntity));


        UserDetails userDetails = userDetailsService.loadUserByUsername("test_user");


        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test_user");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPass");
        assertThat(userDetails.getAuthorities()).extracting("authority")
                .containsExactly("ROLE_USER");

        verify(userRepository, times(1)).findByUsernameWithRole("test_user");
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenUserNotFound() {

        when(userRepository.findByUsernameWithRole(anyString())).thenReturn(Optional.empty());


        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknownUser"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: unknownUser");

        verify(userRepository, times(1)).findByUsernameWithRole("unknownUser");
    }
}
