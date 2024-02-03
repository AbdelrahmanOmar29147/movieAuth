package com.movieApp.authService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieApp.authService.components.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthentcationSecurityFailTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    ObjectMapper mapper;

    @Test
    void validate_fail() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(jwtAuthenticationFilter).build();
        String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNaXJvQHRlc3QuY29tIiwiaWF0IjoxNzA2ODk4OTE0LCJleHAiOjE3MDY5MDAzNTR9.B6y7efHIR7e_6YrWgA872e11TKuiEPC7TinYq79u4Fg";

        assertThrows(Exception.class,
                ()->{
                    mockMvc.perform(MockMvcRequestBuilders
                                    .get("/api/v1/auth/validate")
                                    .header("authorization", "Bearer " + INVALID_TOKEN)
                                    .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().is4xxClientError());
                });
    }
}
