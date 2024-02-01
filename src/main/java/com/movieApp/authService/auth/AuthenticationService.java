package com.movieApp.authService.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieApp.authService.config.CaptchaConfig;
import com.movieApp.authService.config.JwtService;
import com.movieApp.authService.user.Role;
import com.movieApp.authService.user.User;
import com.movieApp.authService.user.UserRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CaptchaConfig captchaConfig;
    private final ObjectMapper mapper;

    private final RestTemplate restTemplate = new RestTemplate();


    public AuthenticationResponse register(RegisterRequest request) throws Exception {
        try{
            if(verifyChaptcha(request.getToken()).isSuccess()){
                var user = User.builder()
                        .firstname(request.getFirstname())
                        .lastname(request.getLastname())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build();
                repository.save(user);
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder().Token(jwtToken).build();
            }
        }
        catch(HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        try{
            if(verifyChaptcha(request.getToken()).isSuccess()) {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
                );
                var user = repository.findByEmail(request.getEmail()).orElseThrow();
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder().Token(jwtToken).build();
            }
        }
        catch(HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public UserDetails validate(HashMap<String, String> headers) {
        String token = headers.get("authorization").substring(7);
        String email =  jwtService.extractUsername(token);
        return repository.findByEmail(email).orElseThrow();
    }

    public RecaptchaResponse verifyChaptcha(String tokenResponse)  {
        ResponseEntity<RecaptchaResponse> responseEntity = null;

        RecaptchaRequestDto requestDto = new RecaptchaRequestDto(captchaConfig.getSecret(), tokenResponse);
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(captchaConfig.getUrl())
                .queryParam("secret", requestDto.getSecret())
                .queryParam("response", requestDto.getResponse());

        try {
            responseEntity = restTemplate.exchange(
                    uriBuilder.buildAndExpand().toUri(),
                    HttpMethod.POST,
                    httpEntity,
                    RecaptchaResponse.class
            );
        }  catch (Exception e) {
            throw new InvalidRequestStateException(e.getMessage());
        }
        return responseEntity.getBody();
    }
}
