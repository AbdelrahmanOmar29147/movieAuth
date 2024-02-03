package com.movieApp.authService.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WireMockConfig {

    @Bean
    public WireMockServer wireMockServer(){
        WireMockServer server= new WireMockServer(9090);
        server.start();
        return server;
    }
}
