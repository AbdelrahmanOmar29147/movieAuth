package com.movieApp.authService.components.utils;

import com.movieApp.authService.client.GoogleRecaptchaClient;
import com.movieApp.authService.model.DTO.RecaptchaRequestDTO;
import com.movieApp.authService.model.DTO.RecaptchaResponseDTO;
import com.movieApp.authService.config.CaptchaConfig;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CaptchaUtil {
    private final CaptchaConfig captchaConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    private final GoogleRecaptchaClient googleRecaptchaClient;

    public RecaptchaResponseDTO verifyCaptcha(String tokenResponse)  {
        ResponseEntity<RecaptchaResponseDTO> recaptchaResponse;
        RecaptchaRequestDTO requestDto = new RecaptchaRequestDTO(captchaConfig.getSecret(), tokenResponse);
        try {
            recaptchaResponse = googleRecaptchaClient.googleRecaptcha(requestDto.getSecret(), requestDto.getResponse());
        }  catch (Exception e) {
            throw new InvalidRequestStateException(e.getMessage());
        }
        return recaptchaResponse.getBody();
    }
}
