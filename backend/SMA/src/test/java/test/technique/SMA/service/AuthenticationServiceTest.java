package test.technique.SMA.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import test.technique.SMA.dto.AdminRegisterRequest;
import test.technique.SMA.dto.LoginRequest;
import test.technique.SMA.dto.LoginResponse;
import test.technique.SMA.entity.Admin;
import test.technique.SMA.exception.InvalidCredentialsException;
import test.technique.SMA.exception.ResourceAlreadyExistsException;
import test.technique.SMA.repository.AdminRepository;
import test.technique.SMA.security.JwtProvider;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private Admin testAdmin;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testAdmin = Admin.builder()
            .id(1L)
            .username("admin")
            .password("hashedPassword")
            .createdAt(LocalDateTime.now())
            .build();

        loginRequest = LoginRequest.builder()
            .username("admin")
            .password("password")
            .build();
    }

    @Test
    void testLoginSuccess() {
        // Arrange
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(
            new UsernamePasswordAuthenticationToken("admin", "password")
        );
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(testAdmin));
        when(jwtProvider.generateToken(any())).thenReturn("jwtToken");

        // Act
        LoginResponse response = authenticationService.login(loginRequest, "127.0.0.1");

        // Assert
        assertNotNull(response);
        assertEquals("admin", response.getUsername());
        assertEquals("jwtToken", response.getToken());
        assertEquals("Bearer", response.getType());
        verify(rateLimitService).resetAttempts("admin");
    }

    @Test
    void testLoginFailedTooManyAttempts() {
        // Arrange
        when(rateLimitService.isBlocked(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.login(loginRequest, "127.0.0.1");
        });
    }

    @Test
    void testRegisterSuccess() {
        // Arrange
        AdminRegisterRequest registerRequest = AdminRegisterRequest.builder()
            .username("newadmin")
            .password("password")
            .confirmPassword("password")
            .build();

        when(adminRepository.existsByUsername("newadmin")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashedPassword");
        when(adminRepository.save(any())).thenReturn(testAdmin);
        when(jwtProvider.generateTokenFromUsername("newadmin")).thenReturn("jwtToken");

        // Act
        LoginResponse response = authenticationService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Bearer", response.getType());
        assertEquals("jwtToken", response.getToken());
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void testRegisterPasswordMismatch() {
        // Arrange
        AdminRegisterRequest registerRequest = AdminRegisterRequest.builder()
            .username("newadmin")
            .password("password")
            .confirmPassword("differentpassword")
            .build();

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.register(registerRequest);
        });
    }

    @Test
    void testRegisterUserAlreadyExists() {
        // Arrange
        AdminRegisterRequest registerRequest = AdminRegisterRequest.builder()
            .username("admin")
            .password("password")
            .confirmPassword("password")
            .build();

        when(adminRepository.existsByUsername("admin")).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> {
            authenticationService.register(registerRequest);
        });
    }
}
