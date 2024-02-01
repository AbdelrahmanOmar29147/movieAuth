package com.movieApp.authService.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecaptchaResponse {

    private boolean success;

    private String challengeTs;

    private String action;

    private String hostname;

    private double score;

    private List<String> errorCodes;

}
