package com.movieApp.authService.auth;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.movieApp.authService.config.JwtAuthenticationFilter;
import com.movieApp.authService.config.JwtService;
import com.movieApp.authService.config.WireMockConfig;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
public class AuthenticationControllerSecurityTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockMvc mockMvc;
    private User user;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserDetailsService userDetailsService;

    @MockBean
    UserRepository userRepository;

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
}
