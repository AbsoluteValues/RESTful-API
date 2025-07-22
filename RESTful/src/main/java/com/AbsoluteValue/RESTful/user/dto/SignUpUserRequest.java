package com.AbsoluteValue.RESTful.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpUserRequest(

        @NotBlank
        @Size(min = 1, max = 10)
        String id,

        @NotBlank
        @Size(min = 1, max = 500)
        String password,

        @NotBlank
        @Size(min = 1, max = 10)
        String nickname
) {
}