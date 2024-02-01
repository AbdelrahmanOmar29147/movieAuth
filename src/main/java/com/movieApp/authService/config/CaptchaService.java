package com.movieApp.authService.config;

import com.movieApp.authService.auth.RecaptchaRequestDto;
import com.movieApp.authService.auth.RecaptchaResponse;
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
public class CaptchaService {
    private final CaptchaConfig captchaConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public RecaptchaResponse verifyChaptcha(String tokenResponse)  {
        ResponseEntity<RecaptchaResponse> responseEntity = null;

        RecaptchaRequestDto requestDto = new RecaptchaRequestDto(captchaConfig.getSecret(), tokenResponse);
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(captchaConfig.getUrl())
                .queryParam("secret", requestDto.getSecret())
                .queryParam("response", requestDto.getResponse());
        System.out.println(uriBuilder.buildAndExpand().toUri());
        try {
            responseEntity = restTemplate.exchange(
                    uriBuilder.buildAndExpand().toUri(),
                    HttpMethod.POST,
                    httpEntity,
                    RecaptchaResponse.class
            );
            System.out.println(responseEntity.getBody().getAction());
            System.out.println(responseEntity.getStatusCode());

        }  catch (Exception e) {
            throw new InvalidRequestStateException(e.getMessage());
        }
        return responseEntity.getBody();
    }
}
