package com.nhnacademy.bookstore.member.auth.service;

import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.member.auth.repository.AuthRepository;
import com.nhnacademy.bookstore.member.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Auth auth;

    @BeforeEach
    void setUp() {
        auth = new Auth(1L, "ROLE_USER");
    }

    @Test
    void testSave() {
        // Given
        when(authRepository.save(any(Auth.class))).thenReturn(auth);

        // When
        authService.save(auth);

        // Then
        verify(authRepository, times(1)).save(auth);
    }

    @Test
    void testGetAuth() {
        // Given
        when(authRepository.findByName(eq("ROLE_USER"))).thenReturn(auth);

        // When
        Auth result = authService.getAuth("ROLE_USER");

        // Then
        verify(authRepository, times(1)).findByName("ROLE_USER");
    }
}
