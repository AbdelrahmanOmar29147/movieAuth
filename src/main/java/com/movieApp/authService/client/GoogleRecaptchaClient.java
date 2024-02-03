package com.movieApp.authService.client;

import com.movieApp.authService.model.DTO.RecaptchaResponseDTO;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "googleClient")
public  interface GoogleRecaptchaClient {

    @PostMapping
    ResponseEntity<RecaptchaResponseDTO> googleRecaptcha(@QueryMap Map<String, String> requestParams);
}
