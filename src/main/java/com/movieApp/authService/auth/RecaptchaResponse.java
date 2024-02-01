package com.movieApp.authService.auth;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RecaptchaResponse {

    private boolean success;

    private String challengeTs;

    private String action;

    private String hostname;

    private double score;

    private List<String> errorCodes;

}
