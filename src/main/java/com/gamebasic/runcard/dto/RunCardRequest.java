package com.gamebasic.runcard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RunCardRequest {
    // NotEmpty, NotBlank 중에 공백 허용 가능성에 대해 피드백 받고 @NotBlank를 고름
    @NotBlank
    private String cardType;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer acquiredFloor;
}
