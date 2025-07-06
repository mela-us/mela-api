package com.hcmus.mela.auth.service;

import com.hcmus.mela.auth.model.User;
import com.hcmus.mela.auth.model.UserRole;
import com.hcmus.mela.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        final User user = authRepository.findByUsername(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("Invalid username or password");
        }

        final String authenticatedUsername = user.getUsername();
        final String authenticatedPassword = user.getPassword();
        final UserRole userRole = user.getUserRole();
        final SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(userRole.name());

        return new org.springframework.security.core.userdetails.User(
                authenticatedUsername,
                authenticatedPassword,
                Collections.singletonList(grantedAuthority));
    }
}
