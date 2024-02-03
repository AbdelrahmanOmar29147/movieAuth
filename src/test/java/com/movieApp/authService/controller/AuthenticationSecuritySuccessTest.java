package com.movieApp.authService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieApp.authService.components.filter.JwtAuthenticationFilter;
import com.movieApp.authService.components.utils.JwtUtil;
import com.movieApp.authService.model.entity.User;
import com.movieApp.authService.repository.UserRepository;
import org.apache.el.parser.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
public class AuthenticationSecuritySuccessTest {
    private MockMvc mockMvc;
    private User user;
    private String TOKEN;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    UserDetailsService userDetailsService;

    @MockBean
    UserRepository userRepository;

    private File getFileFromResource()  {
        return new File(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("user.json")).getFile()
        );
    }

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(jwtAuthenticationFilter).build();
        user = mapper.readValue(getFileFromResource(), User.class);
        TOKEN = "IAMAVERYREALTOKEN";
    }

    @Test
    void validate_success() throws Exception {

        Mockito.when(jwtUtil.extractUsername(TOKEN)).thenReturn(user.getEmail());
        Mockito.when(jwtUtil.isTokenValid(TOKEN, user)).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(user.getEmail())).thenReturn(user);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/auth/validate")
                        .header("authorization", "Bearer " + TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void validate_fail() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(jwtAuthenticationFilter).build();
        Mockito.when(jwtUtil.extractUsername(TOKEN)).thenCallRealMethod();

        assertThrows(Exception.class,
                ()->{
                    mockMvc.perform(MockMvcRequestBuilders
                                    .get("/api/v1/auth/validate")
                                    .header("authorization", "Bearer " + TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().is4xxClientError());
                });
    }
}
