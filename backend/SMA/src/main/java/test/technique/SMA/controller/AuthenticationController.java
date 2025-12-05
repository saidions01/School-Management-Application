package test.technique.SMA.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.technique.SMA.dto.LoginRequest;
import test.technique.SMA.dto.LoginResponse;
import test.technique.SMA.dto.AdminRegisterRequest;
import test.technique.SMA.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API for authentication operations")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login with admin credentials", description = "Authenticate with username and password to get JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Bad request - missing required fields"),
        @ApiResponse(responseCode = "429", description = "Too many login attempts")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        LoginResponse response = authenticationService.login(loginRequest, ipAddress);
        return ResponseEntity.ok(new test.technique.SMA.dto.ApiResponse<>(200, "Login successful", response, System.currentTimeMillis()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new admin", description = "Create a new admin user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Admin registered successfully", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - invalid data"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<?> register(@RequestBody AdminRegisterRequest request) {
        LoginResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new test.technique.SMA.dto.ApiResponse<>(201, "Admin registered successfully", response, System.currentTimeMillis()));
    }
}
