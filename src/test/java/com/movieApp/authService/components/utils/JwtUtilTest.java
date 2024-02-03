package com.movieApp.authService.components.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieApp.authService.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SpringBootTest
class JwtUtilTest {

    private User user;
    private String token;

    @Autowired
    JwtUtil jwtUtil;

    private File getFileFromResource(String filename)  {
        return new File(
                Objects.requireNonNull(this.getClass().getClassLoader().getResource(filename)).getFile()
        );
    }

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setUp() throws IOException {
        user = mapper.readValue(getFileFromResource("user.json"), User.class);
        token = jwtUtil.generateToken(user);
    }

    @Test
    void isTokenValid_success() throws IOException {
        Boolean isValid = jwtUtil.isTokenValid(token, user);
        assert(isValid.equals(true));
    }

    @Test
    void extractUsername() {
        String username = jwtUtil.extractUsername(token);
        assert(Objects.equals(username, user.getEmail()));
    }

}