package com.luxestay.hotel.service;

import com.luxestay.hotel.dto.auth.AuthResponse;
import com.luxestay.hotel.dto.auth.LoginRequest;
import com.luxestay.hotel.model.Account;
import com.luxestay.hotel.model.Role;
import com.luxestay.hotel.repository.AccountRepository;
import com.luxestay.hotel.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder; // Mock đúng class

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private Account account;
    private LoginRequest loginRequest;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("customer");

        account = new Account();
        account.setId(1);
        account.setEmail("test@test.com");
        account.setPasswordHash("hashedPassword");
        account.setFullName("Test User");
        account.setRole(role);
        account.setIsActive(true);
        account.setAvatarUrl(null); // avatarUrl chưa set

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");
    }

    @Test
    void testLogin_Success() {
        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(loginRequest.getPassword(), account.getPasswordHash())).thenReturn(true);

        AuthResponse authResponse = authService.login(loginRequest);

        assertNotNull(authResponse);
        assertNotNull(authResponse.getToken());
        assertEquals(account.getId(), authResponse.getAccountId());
        assertEquals(account.getFullName(), authResponse.getFullName());
        assertEquals(account.getRole().getName(), authResponse.getRole());
        assertNull(authResponse.getAvatarUrl());
    }

    @Test
    void testLogin_UserNotFound() {
        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Tài khoản không tồn tại", exception.getMessage());
    }

    @Test
    void testLogin_InactiveAccount() {
        account.setIsActive(false);
        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(account));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Tài khoản đã bị khóa", exception.getMessage());
    }

    @Test
    void testLogin_IncorrectPassword() {
        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(loginRequest.getPassword(), account.getPasswordHash())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Mật khẩu không đúng", exception.getMessage());
    }
}