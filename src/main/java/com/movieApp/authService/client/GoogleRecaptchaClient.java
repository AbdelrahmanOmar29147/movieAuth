package com.movieApp.authService.client;

import com.movieApp.authService.model.DTO.RecaptchaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "googleRecaptchaClient")
public  interface GoogleRecaptchaClient {

    @PostMapping
    ResponseEntity<RecaptchaResponseDTO> googleRecaptcha(@RequestParam("secret") String secret, @RequestParam("response") String response);
}
