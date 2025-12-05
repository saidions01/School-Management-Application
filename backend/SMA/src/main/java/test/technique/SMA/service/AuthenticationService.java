package test.technique.SMA.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import test.technique.SMA.dto.LoginRequest;
import test.technique.SMA.dto.LoginResponse;
import test.technique.SMA.dto.AdminRegisterRequest;
import test.technique.SMA.entity.Admin;
import test.technique.SMA.exception.InvalidCredentialsException;
import test.technique.SMA.exception.ResourceAlreadyExistsException;
import test.technique.SMA.repository.AdminRepository;
import test.technique.SMA.security.JwtProvider;

@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RateLimitService rateLimitService;

    public LoginResponse login(LoginRequest loginRequest, String ipAddress) {
        String username = loginRequest.getUsername();

        // Check rate limiting
        if (rateLimitService.isBlocked(username)) {
            throw new InvalidCredentialsException("Too many login attempts. Please try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    username,
                    loginRequest.getPassword()
                )
            );

            rateLimitService.resetAttempts(username);

            Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

            String token = jwtProvider.generateToken(authentication);

            return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .username(admin.getUsername())
                .id(admin.getId())
                .build();

        } catch (Exception ex) {
            rateLimitService.recordAttempt(username);
            log.error("Login failed for user: {}", username);
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }

    public LoginResponse register(AdminRegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("Passwords do not match");
        }

        if (adminRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        Admin admin = Admin.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

        Admin savedAdmin = adminRepository.save(admin);

        String token = jwtProvider.generateTokenFromUsername(savedAdmin.getUsername());

        return LoginResponse.builder()
            .token(token)
            .type("Bearer")
            .username(savedAdmin.getUsername())
            .id(savedAdmin.getId())
            .build();
    }
}
