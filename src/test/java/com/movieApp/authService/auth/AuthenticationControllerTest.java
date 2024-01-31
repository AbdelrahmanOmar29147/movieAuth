package com.movieApp.authService.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieApp.authService.config.JwtAuthenticationFilter;
import com.movieApp.authService.config.JwtService;
import com.movieApp.authService.user.Role;
import com.movieApp.authService.user.User;
import com.movieApp.authService.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
class AuthenticationControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private User user;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(jwtAuthenticationFilter).build();
        user = User.builder()
                .firstname("Harry")
                .lastname("Potter")
                .email("harry@gmail.com")
                .password("H123456")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_success() throws Exception {
        Mockito.when(userRepository.save(user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    void authenticate_success() throws Exception {
        AuthenticationRequest request = AuthenticationRequest
                .builder()
                .email("harry@gmail.com")
                .password("H123456")
                .build();

        Mockito.when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.ofNullable(user));
        Mockito.when(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()))
                )
                .thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    void validate_success() throws Exception {
        String INVALID_TOKEN = "IAMAVERYREALTOKEN";

        Mockito.when(jwtService.extractUsername(INVALID_TOKEN)).thenReturn(user.getEmail());
        Mockito.when(jwtService.isTokenValid(INVALID_TOKEN, user)).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(user);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/auth/validate")
                        .header("authorization", "Bearer " + INVALID_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

//    @Test
//    void validateInvalidToken_success() throws Exception {
//        String INVALID_TOKEN = "IAMAVERYREALTOKEN";
//
//        Mockito.when(jwtService.extractUsername(INVALID_TOKEN)).thenReturn(user.getEmail());
//        Mockito.when(jwtService.isTokenValid(INVALID_TOKEN, user)).thenReturn(true);
//        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/v1/auth/validate")
//                        .header("authorization", "Bearer " + INVALID_TOKEN)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
}