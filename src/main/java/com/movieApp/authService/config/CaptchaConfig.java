package com.movieApp.authService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "google.recaptcha")
public class CaptchaConfig {

    private String secret;
    private double scoreThreshold;

}
