package com.movieApp.authService.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecaptchaRequestDto {
    String secret;
    String response;
}
