package com.movieApp.authService.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.movieApp.authService.config.*;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@ContextConfiguration(classes = {WireMockConfig.class})
class AuthenticationControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private WireMockServer wireMockServer;

    private MockMvc mockMvc;
    private User user;
    private RecaptchaResponse response;

    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CaptchaConfig captchaConfig;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CaptchaService captchaService;

    @MockBean
    AuthenticationManager authenticationManager;

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
        response = RecaptchaResponse.builder()
                .success(true)
                .action("duck")
                .build();
    }

    @Test
    void register_success() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Harry")
                .lastname("Potter")
                .email("harry@gmail.com")
                .token("RECAPTCHARESPONSE")
                .password("H123456")
                .build();

        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(captchaService.verifyChaptcha(request.getToken())).thenReturn(response);
        System.out.println(wireMockServer.isHttpEnabled());

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
        AuthenticationRequest request = AuthenticationRequest
                .builder()
                .email("harry@gmail.com")
                .password("H123456")
                .token("")
                .build();

        Mockito.when(captchaService.verifyChaptcha(request.getToken())).thenReturn(response);
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
}