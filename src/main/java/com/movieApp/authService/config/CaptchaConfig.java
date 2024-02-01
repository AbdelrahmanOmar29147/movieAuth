package com.movieApp.authService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.recaptcha")
@Data
public class CaptchaConfig {

    private String secret;
    private String url;
    private double scoreThreshold;

}
