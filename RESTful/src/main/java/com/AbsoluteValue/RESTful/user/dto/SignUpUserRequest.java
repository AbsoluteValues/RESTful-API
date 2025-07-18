package com.AbsoluteValue.RESTful.user.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record SignUpUserRequest(

        @NotBlank
        @DecimalMin(value = "1")
        @DecimalMax(value = "10")
        String id,

        @NotBlank
        @DecimalMin(value = "1")
        @DecimalMax(value = "500")
        String password,

        @NotBlank
        @DecimalMin(value = "1")
        @DecimalMax(value = "10")
        String nickname
) {
}