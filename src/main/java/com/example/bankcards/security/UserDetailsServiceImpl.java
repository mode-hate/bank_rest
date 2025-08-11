package com.example.bankcards.security;

import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsernameWithRole(username)
                //org.springframework.security.core.userdetails.User
                .map(entity -> new User(
                        entity.getUsername(),
                        entity.getPassword(),
                        List.of(new SimpleGrantedAuthority(entity.getRole().getName())))
                )
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}