
package com.flightapp.user_service.controller;

//import com.flightapp.user_service.dto.LoginRequest;
//import com.flightapp.user_service.dto.MessageResponse;
//import com.flightapp.user_service.dto.SignupRequest;
//import com.flightapp.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/flight/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

  
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signupRequest) {
        String response = authService.registerUser(signupRequest);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        String response = authService.refreshJwtToken(refreshToken);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String response = authService.logoutUser();
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @PostMapping("/block-user")
    public ResponseEntity<?> blockUser(
            @RequestParam String username,
            @RequestParam boolean block,
            @RequestParam String adminName
    ) {
        String response = authService.blockUser(username, block, adminName);
        return ResponseEntity.ok(new MessageResponse(response));
    }
}

