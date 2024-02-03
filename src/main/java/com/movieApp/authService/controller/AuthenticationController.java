package com.movieApp.authService.controller;

import com.movieApp.authService.auth.AuthenticationRequest;
import com.movieApp.authService.auth.AuthenticationResponse;
import com.movieApp.authService.auth.AuthenticationService;
import com.movieApp.authService.auth.RegisterRequest;
import com.movieApp.authService.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) throws Exception {
        return ResponseEntity.ok(service.register(request));
    }

    @CrossOrigin
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) throws Exception {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<UserDetails> validate(
            @RequestHeader HashMap<String,String> headers
    ) {
        return ResponseEntity.ok(service.validate(headers));
    }

}
