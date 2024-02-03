package com.movieApp.authService.service;

import com.movieApp.authService.auth.AuthenticationRequestDTO;
import com.movieApp.authService.auth.AuthenticationResponseDTO;
import com.movieApp.authService.auth.RegisterRequestDTO;
import com.movieApp.authService.config.CaptchaUtil;
import com.movieApp.authService.config.JwtUtil;
import com.movieApp.authService.user.Role;
import com.movieApp.authService.user.User;
import com.movieApp.authService.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final CaptchaUtil captchaUtil;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponseDTO register(RegisterRequestDTO request) throws Exception {
        try{
            if(captchaUtil.verifyCaptcha(request.getToken()).isSuccess()){
                var user = User.builder()
                        .firstname(request.getFirstname())
                        .lastname(request.getLastname())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build();
                repository.save(user);
                var jwtToken = jwtUtil.generateToken(user);
                return AuthenticationResponseDTO.builder().Token(jwtToken).build();
            }
        }
        catch(HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) throws Exception {
        try{
            if(captchaUtil.verifyCaptcha(request.getToken()).isSuccess()) {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );
                var user = repository.findByEmail(request.getEmail()).orElseThrow();
                var jwtToken = jwtUtil.generateToken(user);
                return AuthenticationResponseDTO.builder().Token(jwtToken).build();
            }
        }
        catch(HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public UserDetails validate(HashMap<String, String> headers) {
        String token = headers.get("authorization").substring(7);
        String email =  jwtUtil.extractUsername(token);
        return repository.findByEmail(email).orElseThrow();
    }
}
