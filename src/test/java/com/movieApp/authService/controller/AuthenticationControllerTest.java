package com.movieApp.authService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.movieApp.authService.model.DTO.AuthenticationRequestDTO;
import com.movieApp.authService.model.DTO.RecaptchaResponseDTO;
import com.movieApp.authService.model.DTO.RegisterRequestDTO;
import com.movieApp.authService.components.filter.JwtAuthenticationFilter;
import com.movieApp.authService.config.*;
import com.movieApp.authService.model.entity.User;
import com.movieApp.authService.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@ContextConfiguration(classes = {WireMockConfig.class})
@TestPropertySource(locations= "classpath:application-test.properties")
class AuthenticationControllerTest {

    private MockMvc mockMvc;
    private User user;
    private RecaptchaResponseDTO recaptchaResponse;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AuthenticationManager authenticationManager;

    private File getFileFromResource(String filename)  {
        return new File(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource(filename)).getFile()
        );
    }

    private void createGoogleRecaptchaWireMock(String token, Integer status) throws JsonProcessingException {
        this.wireMockServer.stubFor(
                post("/google?secret=RECAPTCHASECRET&response=" + token)
                        .willReturn(
                                aResponse()
                                        .withStatus(status)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                        .withBody(mapper.writeValueAsString(recaptchaResponse))
                        )
        );
    }

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(jwtAuthenticationFilter).build();
        user = mapper.readValue(getFileFromResource("user.json"), User.class);
        recaptchaResponse = new RecaptchaResponseDTO();
    }

    @Test
    void register_success() throws Exception {
        RegisterRequestDTO request = mapper.readValue(getFileFromResource("registerRequestDTO.json"), RegisterRequestDTO.class);
        recaptchaResponse.setSuccess(true);
        createGoogleRecaptchaWireMock(request.getToken(), 200);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    void authenticate_success() throws Exception {
        AuthenticationRequestDTO request = mapper
                .readValue(getFileFromResource("authenticationRequestDTO.json"), AuthenticationRequestDTO.class);
        recaptchaResponse.setSuccess(true);
        createGoogleRecaptchaWireMock(request.getToken(), 200);

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
    void authenticateRecaptcha_fail() throws Exception {
        AuthenticationRequestDTO request = mapper
                .readValue(getFileFromResource("authenticationRequestDTO.json"), AuthenticationRequestDTO.class);
        recaptchaResponse.setSuccess(false);
        createGoogleRecaptchaWireMock(request.getToken(), 200);

        assertThrows(Exception.class,
                ()->{
                    mockMvc.perform(MockMvcRequestBuilders
                                    .post("/api/v1/auth/authenticate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(this.mapper.writeValueAsString(request))
                            )
                            .andExpect(status().is4xxClientError())
                            .andExpect(jsonPath("$").doesNotExist());
                });
    }

    @Test
    void registerRecaptcha_fail() throws Exception {
        RegisterRequestDTO request = mapper.readValue(getFileFromResource("registerRequestDTO.json"), RegisterRequestDTO.class);
        recaptchaResponse.setSuccess(false);
        createGoogleRecaptchaWireMock(request.getToken(), 200);

        assertThrows(Exception.class,
                ()->{
                    mockMvc.perform(MockMvcRequestBuilders
                                    .post("/api/v1/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(this.mapper.writeValueAsString(request)))
                            .andExpect(status().is4xxClientError())
                            .andExpect(jsonPath("$").doesNotExist());
                });
    }

    @Test
    void registerRecaptcha403_fail() throws Exception {
        RegisterRequestDTO request = mapper.readValue(getFileFromResource("registerRequestDTO.json"), RegisterRequestDTO.class);
        recaptchaResponse.setSuccess(false);
        createGoogleRecaptchaWireMock(request.getToken(), 403);


        assertThrows(Exception.class,
                ()->{
                    mockMvc.perform(MockMvcRequestBuilders
                                    .post("/api/v1/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(this.mapper.writeValueAsString(request)))
                            .andExpect(status().is4xxClientError())
                            .andExpect(jsonPath("$").doesNotExist());
                });
    }
}