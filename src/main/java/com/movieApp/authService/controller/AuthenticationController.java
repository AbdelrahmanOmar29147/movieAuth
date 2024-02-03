package com.movieApp.authService.controller;

import com.movieApp.authService.model.DTO.AuthenticationRequestDTO;
import com.movieApp.authService.model.DTO.AuthenticationResponseDTO;
import com.movieApp.authService.service.AuthenticationService;
import com.movieApp.authService.model.DTO.RegisterRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody RegisterRequestDTO request
    ) throws Exception {
        return ResponseEntity.ok(service.register(request));
    }

    @CrossOrigin
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request
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
