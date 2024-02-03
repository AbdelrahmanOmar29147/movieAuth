package com.movieApp.authService.model.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RecaptchaResponseDTO {

    private boolean success;

    private String challengeTs;

    private String action;

    private String hostname;

    private double score;

    private List<String> errorCodes;

}
