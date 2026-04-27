package com.flightapp.user_service.service;

import com.flightapp.user_service.model.User;
import com.flightapp.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplementationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImplementation userDetailsService;

    @Test
    public void testLoadUserByUsernameSuccessfull() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRoles(Collections.emptySet());
        user.setBlocked(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDetails result = userDetailsService.loadUserByUsername(username);
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    public void testLoadUserByNonExistentUsername() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }

    @Test
    public void testLoadUserByBlockedUsername() {
        String username = "blockeduser";
        User user = new User();
        user.setUsername(username);
        user.setBlocked(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        assertThrows(LockedException.class, () -> userDetailsService.loadUserByUsername(username));
    }
}
