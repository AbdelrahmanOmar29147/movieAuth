package com.movieApp.authService.components.utils;

import com.movieApp.authService.auth.RecaptchaRequestDTO;
import com.movieApp.authService.auth.RecaptchaResponseDTO;
import com.movieApp.authService.config.CaptchaConfig;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CaptchaUtil {
    private final CaptchaConfig captchaConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public RecaptchaResponseDTO verifyCaptcha(String tokenResponse)  {
        ResponseEntity<RecaptchaResponseDTO> responseEntity = null;

        RecaptchaRequestDTO requestDto = new RecaptchaRequestDTO(captchaConfig.getSecret(), tokenResponse);
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(captchaConfig.getUrl())
                .queryParam("secret", requestDto.getSecret())
                .queryParam("response", requestDto.getResponse());
        try {
            responseEntity = restTemplate.exchange(
                    uriBuilder.buildAndExpand().toUri(),
                    HttpMethod.POST,
                    httpEntity,
                    RecaptchaResponseDTO.class
            );
        }  catch (Exception e) {
            throw new InvalidRequestStateException(e.getMessage());
        }
        return responseEntity.getBody();
    }
}
