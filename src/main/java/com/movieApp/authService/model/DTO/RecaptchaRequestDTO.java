package com.movieApp.authService.auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecaptchaRequestDTO {
    String secret;
    String response;
}
